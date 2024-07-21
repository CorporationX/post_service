package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.WrongTimeException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.dto.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;
    private final ExecutorService executorService;
    private final PostMapper postMapper;

    @Value("${moderation.chunkSize}")
    private int chunkSize;

    @Override
    @Async("moderatorExecutor")
    public void moderatePosts() {
        List<Post> moderatingPosts = postRepository.findAllByVerifiedAtIsNull();
        CompletableFuture.supplyAsync(() -> createSubLists(moderatingPosts))
                .thenAcceptAsync(posts -> posts.stream()
                        .flatMap(Collection::stream)
                        .forEach(post -> {
                            post.setVerified(moderationDictionary.inspect(post.getContent()));
                            post.setVerifiedAt(LocalDateTime.now());
                        }));
    }

    @Override
    public Post getPostById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
    }

    private List<List<Post>> createSubLists(List<Post> posts) {
        List<List<Post>> chunks = new ArrayList<>();
        for (int index = 0; index < posts.size(); index += chunkSize) {
            List<Post> chunk = new ArrayList<>(posts.subList(index, Math.min(posts.size(), index + chunkSize)));
            chunks.add(chunk);
        }
        return chunks;
    }

    public void publishScheduledPosts() {
        List<Post> postsToPublish = postRepository.findReadyToPublish();
        int batchSize = 1000;
        List<List<Post>> postBatches = ListUtils.partition(postsToPublish, batchSize);
        postBatches.forEach(batch ->
                CompletableFuture.runAsync(() -> publishPostBatch(batch), executorService)
        );
    }

    @Override
    public boolean existsById(long id) {
        return postRepository.existsById(id);
    }

    private void publishPostBatch(List<Post> postBatch) {
        postBatch.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        });
        postRepository.saveAll(postBatch);
    }

    public Post createPost(PostDto postDto) {
        if (postDto.getScheduledAt() != null && postDto.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new WrongTimeException("Запланированное время не может быть в прошлом");
        }
        Post post = postMapper.toEntity(postDto);
        setPublishedFields(post, postDto.getScheduledAt());
        return postRepository.save(post);
    }

    private void setPublishedFields(Post post, LocalDateTime scheduledAt) {
        if (scheduledAt != null) {
            post.setPublished(false);
            post.setPublishedAt(scheduledAt);
        } else {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }
    }
}