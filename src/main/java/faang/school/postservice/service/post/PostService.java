package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.EventType;
import faang.school.postservice.model.event.PostViewedEvent;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.publisher.KafkaEventPublisher;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final EventPublisher eventPublisher;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ExecutorService postPublishingExecutor;
    private final KafkaEventPublisher kafkaPublisher;

    private final static int BATCH_SIZE = 1000;


    public PostDto createDraftPost(PostDto postDto) {
        validateOwner(postDto.getAuthorId(), postDto.getProjectId());
        Post post = postMapper.toEntity(postDto);
        post.setAuthorId(postDto.getAuthorId());
        post.setProjectId(postDto.getProjectId());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto publishPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with id " + postId + " is not exists"));

        validateDeleted(post);
        validatePublished(post, false);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        long authorId = post.getAuthorId() == null ? post.getProjectId() : post.getAuthorId();

        eventPublisher.publish(EventDto.builder()
                .eventId(postId)
                .authorId(authorId)
                .eventType(EventType.PUBLISHED_POST)
                .build());

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(long postId, PostDto postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with id " + postId + " is not exists"));
        validateDeleted(post);

        postMapper.updatePostFromDto(postDto, post);
        return postMapper.toDto(post);
    }

    @Transactional
    public void deletePost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with id " + postId + " is not exists"));
        validateDeleted(post);
        post.setDeleted(true);
    }

    public PostDto getPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("post with id " + postId + " is not exists"));
        validateDeleted(post);
        validatePublished(post, true);

        PostViewedEvent postViewedEvent =PostViewedEvent.builder()
                .postId(post.getId())
                .viewedAt(LocalDateTime.now()).build();

        kafkaPublisher.publish(EventType.POST_VIEWS, postViewedEvent);

        return postMapper.toDto(post);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
    }

    public boolean existsById(Long postId) {
        return postRepository.existsById(postId);
    }

    public List<PostDto> getAllAuthorDraftPosts(long authorId) {
        validExistsAuthor(authorId);
        return getAllFilterAndSortedPosts(postRepository.findByAuthorId(authorId), Predicate.not(Post::isPublished));
    }

    public List<PostDto> getAllAuthorPosts(long authorId) {
        validExistsAuthor(authorId);
        return getAllFilterAndSortedPosts(postRepository.findByAuthorId(authorId), Post::isPublished);
    }

    public List<PostDto> getAllProjectDraftPosts(long projectId) {
        validateExistsProject(projectId);
        return getAllFilterAndSortedPosts(postRepository.findByProjectId(projectId), Predicate.not(Post::isPublished));
    }

    public List<PostDto> getAllProjectPosts(long projectId) {
        validateExistsProject(projectId);
        return getAllFilterAndSortedPosts(postRepository.findByProjectId(projectId), Post::isPublished);
    }

    public void publishScheduledPosts(){
        List<Post> posts = postRepository.findReadyToPublish();
        if(posts.isEmpty()){
            log.info("Нет постов для публикации");
            return;
        }

        LocalDateTime now = LocalDateTime.now();


        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < posts.size(); i+= BATCH_SIZE) {
            List<Post> chunk = posts.subList(i,Math.min(i+ BATCH_SIZE, posts.size()));

            int chunkNumber = i / BATCH_SIZE +1;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                log.info("Начинаем обработку чанка {} ({} постов) в потоке: {}",
                        chunkNumber, chunk.size(), Thread.currentThread().getName());
                chunk.forEach(post -> {
                    post.setPublished(true);
                    post.setPublishedAt(now);
                });
                postRepository.saveAll(chunk);
            }, postPublishingExecutor);
            futures.add(future);
        }

        log.info("🌀 Ожидаем завершения всех задач публикации...");

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("✅ Все посты успешно опубликованы!");
    }


    public List<PostDto> getAllDraftPosts() {
        List<Post> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return getAllFilterAndSortedPosts(posts, Predicate.not(Post::isPublished));
    }
  
    private List<PostDto> getAllFilterAndSortedPosts(List<Post> posts, Predicate<Post> publishedFilter) {
        return posts.stream()
                .filter(Predicate.not(Post::isDeleted))
                .filter(publishedFilter)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private void validateDeleted(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("post with id " + post.getId() +" has been deleted.");
        }
    }

    private static void validatePublished(Post post, boolean expected) {
        if (post.isPublished() != expected) {
            throw new DataValidationException("post with id " + post.getId() +
                    (expected ? " should be published" : " has already been published"));
        }
    }

    private void validateOwner(Long authorId, Long projectId)  {
        if (authorId != null && projectId != null) {
            throw new DataValidationException("author or project should be null");
        }

        if (authorId == null && projectId == null) {
            throw new DataValidationException("author or project should not be null");
        }

        validExistsAuthor(authorId);
        validateExistsProject(projectId);
    }

    private void validExistsAuthor(Long authorId) {
        if (authorId != null) {
            try {
                userServiceClient.getUser(authorId);
            } catch (FeignException e) {
                throw new EntityNotFoundException("user with id " + authorId + " is not exists");
            }

        }
    }

    private void validateExistsProject(Long projectId) {
        if (projectId != null) {
            try {
                projectServiceClient.getProject(projectId);
            } catch (FeignException e) {
                throw new EntityNotFoundException("project with id " + projectId + " is not exists");
            }
        }
    }
}
