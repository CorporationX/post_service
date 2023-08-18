package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.redis.PostViewEventMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.RedisMessagePublisher;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;
    private final ObjectMapper objectMapper;
    private final RedisMessagePublisher redisMessagePublisher;
    private final PostViewEventMapper postViewEventMapper;
    private final ChannelTopic postViewTopic;

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));
    }

    @Transactional
    public PostDto createPost(PostDto post) {
        ProjectDto project = null;
        UserDto user = null;

        if (post.getProjectId() != null) {
            project = projectServiceClient.getProject(post.getProjectId());
        } else if (post.getAuthorId() != null) {
            user = userServiceClient.getUser(post.getAuthorId());
        }

        postValidator.validatePostCreator(post, project, user);
        postValidator.validatePostContent(post);

        Post postEntity = postMapper.toPost(post);

        return postMapper.toDto(postRepository.save(postEntity));
    }

    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = getPostById(postId);

        postValidator.validatePublishPost(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postUpdateDto) {
        Post post = getPostById(postUpdateDto.getId());
        postValidator.validationOfPostUpdate(postUpdateDto, post);

        Post updatedPost = postMapper.toPost(postUpdateDto);

        return postMapper.toDto(postRepository.save(updatedPost));
    }

    @Transactional(readOnly = true)
    public PostDto getPost(Long postId) {
        Post post = getPostById(postId);
        PostViewEventDto postViewEventDto = postViewEventMapper.toDto(post);
        postViewEventDto.setCreatedAt(LocalDateTime.now());

        publishEventToChannel(postViewEventDto);

        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedDraftsByAuthorId(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        postValidator.validateAuthor(user);
        List<Post> draftsByAuthorId = postRepository.findDraftsByAuthorId(user.getId());
        return getSortedDrafts(draftsByAuthorId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedDraftsByProjectId(Long projectId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        postValidator.validateProject(project);
        List<Post> draftsByProjectId = postRepository.findDraftsByProjectId(project.getId());
        return getSortedDrafts(draftsByProjectId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedPublishedPostsByAuthorId(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        postValidator.validateAuthor(user);
        List<Post> publishedPostsByAuthorId = postRepository.findPublishedPostsByAuthorId(user.getId());

        publishedPostsByAuthorId.forEach(post -> {
            PostViewEventDto postViewEventDto = postViewEventMapper.toDto(post);
            postViewEventDto.setCreatedAt(LocalDateTime.now());

            publishEventToChannel(postViewEventDto);
        });

        return getSortedPublishedPosts(publishedPostsByAuthorId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(Long projectId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        postValidator.validateProject(project);
        List<Post> publishedPostsByProjectId = postRepository.findPublishedPostsByProjectId(project.getId());

        publishedPostsByProjectId.forEach(post -> {
            PostViewEventDto postViewEventDto = postViewEventMapper.toDto(post);
            postViewEventDto.setCreatedAt(LocalDateTime.now());

            publishEventToChannel(postViewEventDto);
        });

        return getSortedPublishedPosts(publishedPostsByProjectId);
    }

    @Transactional
    public boolean softDeletePost(Long postId) {
        Post post = getPostById(postId);

        postValidator.validationOfPostDelete(post);

        post.setDeleted(true);
        postRepository.save(post);
        return true;
    }

    private List<PostDto> getSortedDrafts(List<Post> draftsByAuthorId) {
        return draftsByAuthorId.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPublishedPosts(List<Post> publishedPostsByAuthorId) {
        return publishedPostsByAuthorId.stream()
                .sorted(Comparator.comparing(Post::getPublishedAt))
                .map(postMapper::toDto)
                .toList();
    }

    private void publishEventToChannel(PostViewEventDto postViewEventDto) {
        try {
            String postViewEvent = objectMapper.writeValueAsString(postViewEventDto);
            redisMessagePublisher.publish(postViewTopic.getTopic(), postViewEvent);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }

        log.info("Post id={} was viewed by user id={} at {}",
                postViewEventDto.getPostId(), postViewEventDto.getUserId(), postViewEventDto.getCreatedAt());
    }
}
