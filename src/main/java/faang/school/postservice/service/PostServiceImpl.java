package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl {

    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ModerationService moderationService;
    @Value("${post.moderation.batchSize}")
    private int batchSize;

    @Transactional
    public PostDto createDraft(PostDto postDto) {
        validateAuthor(postDto);
        Post post = postMapper.toEntity(postDto);
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publish(long id) {
        Post post = searchPostById(id);
        postIsPublished(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto update(PostDto postDto) {
        Post post = searchPostById(postDto.getId());
        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(long id) {
        Post post = searchPostById(id);
        post.setPublished(false);
        post.setDeleted(true);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto getPostById(long id) {
        return postMapper.toDto(searchPostById(id));
    }

    @Transactional
    public List<PostDto> getDraftsByAuthorId(long id) {
        List<Post> posts = postRepository.findByAuthorId(id);
        return filterPosts(posts, false);
    }

    @Transactional
    public List<PostDto> getDraftsByProjectId(long id) {
        List<Post> posts = postRepository.findByProjectId(id);
        return filterPosts(posts, false);
    }

    @Transactional
    public List<PostDto> getPostsByAuthorId(long id) {
        List<Post> posts = postRepository.findByAuthorId(id);
        return filterPosts(posts, true);
    }

    @Transactional
    public List<PostDto> getPostsByProjectId(long id) {
        List<Post> posts = postRepository.findByProjectId(id);
        return filterPosts(posts, true);
    }

    @Transactional
    public void checkPostsWithBadWord() {
        List<Post> allPosts = postRepository.findAllByVerifiedAtNull();
        List<List<Post>> postPartitions = ListUtils.partition(allPosts, batchSize);
        log.info("Moderation of posts for offensive content starts");
        postPartitions.forEach(posts -> moderationService.moderatePosts(posts));
        log.info("Post moderation is complete");
    }

    public Post searchPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Post with id " + id + " not found."));
    }

    private void validateAuthor(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("The author of the post is not specified");
        }
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("A post cannot have two authors");
        }
        if (postDto.getAuthorId() != null && !userServiceClient.existById(postDto.getAuthorId())) {
            throw new DataValidationException("There is no author with this id " + postDto.getAuthorId());
        }
    }

    private void postIsPublished(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("The post has already been published");
        }
    }

    private List<PostDto> filterPosts(List<Post> posts, boolean isPublished) {
        return posts.stream()
                .filter(post -> post.isPublished() == isPublished)
                .filter(post -> !post.isDeleted())
                .sorted((post1, post2) -> {
                    LocalDateTime date1 = isPublished ? post1.getPublishedAt() : post1.getCreatedAt();
                    LocalDateTime date2 = isPublished ? post2.getPublishedAt() : post2.getCreatedAt();
                    if (date1 == null || date2 == null) {
                        throw new DataValidationException("Invalid date");
                    }
                    return date2.compareTo(date1);
                })
                .map(postMapper::toDto)
                .toList();
    }
}