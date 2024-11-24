package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.event.like.PostViewEvent;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.publisher.post.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${post.moderation.sublist.length}")
    private Long sublistLength;

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ModerationDictionary moderationDictionary;
    private final ExecutorService executor;
    private final PostViewEventPublisher postViewEventPublisher;
    private final UserContext userContext;
    private final KafkaPostViewProducer kafkaPostViewProducer;

    public PostResponseDto getPost(long postId) {
        Post post = findById(postId);

        publishEvent(postId, post.getAuthorId());
        publishEventInKafka(post);

        return postMapper.toResponseDto(post, post.getLikes().size());
    }

    public List<Post> getAllPostsNotPublished() {
        return postRepository.findReadyToPublish();
    }

    public void savePosts(List<Post> posts) {
        postRepository.saveAll(posts);
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post service. Post not found. id: " + postId));
    }

    public List<PostResponseDto> getPostsByAuthorWithLikes(long authorId) {
        List<Post> posts = postRepository.findByAuthorIdWithLikes(authorId);
        return posts.stream()
                .map(post -> postMapper.toResponseDto(post, post.getLikes().size()))
                .toList();
    }

    public List<PostResponseDto> getPostsByProjectWithLikes(long projectId) {
        List<Post> posts = postRepository.findByProjectIdWithLikes(projectId);
        return posts.stream()
                .map(post -> postMapper.toResponseDto(post, post.getLikes().size()))
                .toList();
    }

    public List<Long> findAuthorsWithUnverifiedPosts(int limit, LocalDate fromDate) {
        return postRepository.findAuthorsWithUnverifiedPosts(limit, fromDate);
    }

    @Async("executor")
    public void moderatePostsContent() {
        List<Post> unverifiedPosts = postRepository.findReadyToVerified();

        for (int i = 0; i < unverifiedPosts.size(); i += sublistLength) {
            List<Post> subList = unverifiedPosts.subList(i, (int) Math.min(unverifiedPosts.size(), i + sublistLength));

            CompletableFuture<Void> verifiedEntities =
                    CompletableFuture.runAsync(() -> moderationDictionary.searchSwearWords(subList), executor);

            verifiedEntities.thenAccept(result -> postRepository.saveAll(subList));
        }
    }

    private void publishEvent(Long postId, Long postAuthorId) {
        PostViewEvent postViewEvent = PostViewEvent.builder()
                .postId(postId)
                .authorId(postAuthorId)
                .userId(userContext.getUserId())
                .viewTime(LocalDateTime.now())
                .build();
        try {
            postViewEventPublisher.publish(postViewEvent);
        } catch (Exception ex) {
            log.error("Failed to send notification with postViewEvent: {}", postViewEvent.toString(), ex);
        }
    }

    private void publishEventInKafka(Post post) {
        long postId = post.getId();

        log.debug("publishEventInKafka() start, postId - {}", postId);

        PostViewEvent postViewEvent = PostViewEvent.builder()
                .postId(postId)
                .authorId(post.getAuthorId())
                .userId(userContext.getUserId())
                .viewTime(LocalDateTime.now())
                .build();

        kafkaPostViewProducer.sendMessage(postViewEvent);

        log.debug("publishEventInKafka() finish, postId - {}", postId);
    }
}
