package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.producer.KafkaPostProducer;
import faang.school.postservice.config.kafka.producer.KafkaSubsProducer;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.SubsDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.UserRedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private final KafkaSubsProducer kafkaSubsProducer;
    private final UserRedisService userRedisService;
    private List<List<Long>> followeeLists;

    @Transactional
    public PostDto createDraft(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            return createPostToAuthor(postDto);
        } else {
            return createPostToProject(postDto);
        }
    }

    @Transactional
    public PostDto publishDraft(Long postId) {
        Post draft = postRepository.findById(postId).orElseThrow(() -> {
            log.info("Post {} not found", postId);
            return new EntityNotFoundException("Post " + postId + " not found");
        });
        if (draft.getPublishedAt() != null) {
            throw new DataValidationException("Post is already published");
        }
        draft.setPublished(true);
        draft.setPublishedAt(LocalDateTime.now());
        sentMessage(draft);
        definitionIntoParts(draft.getAuthorId());
        return postMapper.toDto(draft);
    }

    @Transactional
    public PostDto updatePost(Long id, PostUpdateDto postUpdateDto) {
        Post postToUpdate = getPost(id);
        postToUpdate.setContent(postUpdateDto.getContent());
        return postMapper.toDto(postToUpdate);
    }

    @Transactional
    public void delete(Long id) {
        Post postToDelete = getPost(id);
        postToDelete.setDeleted(true);
    }

    public PostDto getPostById(Long id) {
        Post post = getPost(id);
        return postMapper.toDto(post);
    }

    public List<PostDto> getDraftsByUser(Long id) {
        UserDto user = getUser(id);
        List<Post> postsByAuthor = getFilteredPostsByUser(user.getId(), (post) -> !post.isPublished());
        return postMapper.toDtoList(postsByAuthor);
    }

    public List<PostDto> getDraftsByProject(Long id) {
        ProjectDto projectDto = projectServiceClient.getProject(id);
        List<Post> postsByProject = getFilteredPostsByProject(projectDto.getId(), (post) -> !post.isPublished());
        return postsByProject.stream().map(postMapper::toDto).toList();
    }


    public List<PostDto> getPublishedByUser(Long id) {
        UserDto user = getUser(id);
        List<Post> publishedPostsByAuthor = getFilteredPostsByUser(user.getId(), Post::isPublished);
        return publishedPostsByAuthor.stream().map(postMapper::toDto).toList();
    }

    public List<PostDto> getPublishedByProject(Long id) {
        ProjectDto projectDto = projectServiceClient.getProject(id);
        List<Post> publishedPostsByProject = getFilteredPostsByProject(projectDto.getId(), Post::isPublished);
        return publishedPostsByProject.stream().map(postMapper::toDto).toList();
    }

    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    private List<Post> getFilteredPostsByUser(Long userId, Predicate<Post> filter) {
        return postRepository.findByAuthorId(userId)
                .stream().filter(filter).toList();
    }

    private List<Post> getFilteredPostsByProject(Long projectId, Predicate<Post> filter) {
        return postRepository.findByProjectId(projectId)
                .stream().filter(filter).toList();
    }

    private PostDto createPostToProject(PostDto postDto) {
        projectServiceClient.getProject(postDto.getProjectId());
        return getPostDto(postDto);
    }

    private PostDto createPostToAuthor(PostDto postDto) {
        getUser(postDto.getAuthorId());
        return getPostDto(postDto);
    }

    private void definitionIntoParts(Long userId) {
        UserDto user = getUser(userId);
        userRedisService.saveUser(user);
        if (user.getFollowees().size() < 100) {
            sendListFollowee(user.getFollowees(), user.getId());
        } else {
            splitList(user.getFollowees()).forEach(list -> sendListFollowee(list, user.getId()));
        }
    }

    private PostDto getPostDto(PostDto postDto) {
        Post createdDraft = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(createdDraft);
    }

    private void sentMessage(Post post) {
        kafkaPostProducer.sendMessage(postMapper.toDto(post));
    }

    private List<List<Long>> splitList(List<Long> list) {
        followeeLists = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 100) {
            followeeLists.add(new ArrayList<>(list.subList(i, Math.min(i + 100, list.size()))));
        }
        return followeeLists;
    }

    private void sendListFollowee(List<Long> followee, Long authorId) {
        SubsDto subsDto = SubsDto.builder()
                .authorId(authorId)
                .followees(followee)
                .build();
        kafkaSubsProducer.sendMessage(subsDto);
    }

    private UserDto getUser(Long userId) {
        return userServiceClient.getUser(userId);
    }
}