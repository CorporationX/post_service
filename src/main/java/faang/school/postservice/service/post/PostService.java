package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.publisher.UserBanPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validation.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final ResourceService resourceService;
    private final ExecutorService threadPool;
    private final UserBanPublisher userBanPublisher;
    private final PostViewEventPublisher postViewEventPublisher;

    @Value("${post.publisher.batch-size}")
    private Integer scheduledPostsBatchSize;

    @Value("${post.banner.post-count}")
    private Integer postsCountToBan;

    @Transactional
    public PostDto create(PostDto postDto, MultipartFile[] images) {
        postValidator.validatePostAuthor(postDto);
        postValidator.validateIfAuthorExists(postDto);
        Post post = postRepository.save(postMapper.toEntity(postDto));
        log.info("Post saved: {}", post);
        post.setResources(new ArrayList<>());
        if (images != null) {
            postValidator.validateResourcesCount(images.length);
            for (MultipartFile file : images) {
                Resource resource = resourceService.saveImage(file, post);
                post.getResources().add(resource);
            }
        }
        return postMapper.toDto(post);
    }

    public PostDto getPostById(long userId, long postId) {
        Post post = getPostFromRepository(postId);
        if (post.getAuthorId() != userId) {
            publishPostEvent(userId, post);
        }
        log.info("Post {} view event published", postId);
        return postMapper.toDto(post);
    }

    public PostDto publish(long postId) {
        Post post = getPostFromRepository(postId);
        postValidator.validateIfPostIsPublished(post);
        postValidator.validateIfPostIsDeleted(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        int postsQuantity = posts.size();

        for (int i = 0; i < postsQuantity; i += scheduledPostsBatchSize) {
            final int fromIndex = i;
            int toIndex = Math.min(i + scheduledPostsBatchSize, postsQuantity);

            CompletableFuture.runAsync(() -> {
                List<Post> batch = posts.subList(fromIndex, toIndex);
                batch.forEach(post -> {
                    post.setPublished(true);
                    post.setPublishedAt(LocalDateTime.now());
                });
            }, threadPool);
        }
    }

    @Transactional
    public PostDto update(PostDto postDto, MultipartFile[] images) {
        Post post = getPostFromRepository(postDto.getId());
        postValidator.validateUpdatedPost(post, postDto);
        post.setContent(postDto.getContent());
        if (images != null) {
            postValidator.validateResourcesCount(post.getResources().size(), images.length);
            for (MultipartFile file : images) {
                Resource resource = resourceService.saveImage(file, post);
                post.getResources().add(resource);
            }
        }
        return postMapper.toDto(post);
    }

    public void banUsers() {
        postRepository.findByVerifiedFalse().stream()
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > postsCountToBan)
                .map(Map.Entry::getKey)
                .forEach(userId -> userBanPublisher.publish(new UserEvent(userId)));
    }

    public void delete(long postId) {
        Post post = getPostFromRepository(postId);
        postValidator.validateIfPostIsDeleted(post);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public List<PostDto> getCreatedPostsByAuthorId(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    public List<PostDto> getCreatedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    public List<PostDto> getPublishedPostsByAuthorId(long userId, long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .toList();
        posts.forEach(post -> {
            publishPostEvent(userId, post);
            log.info("Post {} view event published", post.getId());
        });
        return postMapper.toDto(posts);
    }

    public List<PostDto> getPublishedPostsByProjectId(long userId, long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .toList();
        posts.forEach(post -> {
            publishPostEvent(userId, post);
            log.info("Post {} view event published", post.getId());
        });
        return postMapper.toDto(posts);
    }

    @Transactional
    public ResourceDto attachMedia(long postId, MultipartFile mediaFile) {
        Post post = getPostFromRepository(postId);
        return resourceService.attachMediaToPost(mediaFile, post);
    }

    private void publishPostEvent(long userId, Post post) {
        PostViewEvent event = PostViewEvent.builder()
                .postId(post.getId())
                .userId(userId)
                .build();
        if (post.getAuthorId() != null) {
            event.setAuthorId(post.getAuthorId());
        }
        if (post.getProjectId() != null) {
            event.setAuthorId(post.getProjectId());
        }
        try {
            postViewEventPublisher.publish(event);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException was thrown", e);
            throw new SerializationException(e.getMessage());
        }
    }

    private Post getPostFromRepository(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post doesn't exist by id: " + postId));
    }
}
