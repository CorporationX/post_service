package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class PostService {

    private Long sublistLength;

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ModerationDictionary moderationDictionary;
    private final ExecutorService executor;

    public PostService(@Value("${post.moderation.sublist.length}") Long sublistLength,
                       PostMapper postMapper,
                       PostRepository postRepository,
                       ModerationDictionary moderationDictionary,
                       ExecutorService executor) {
        this.sublistLength = sublistLength;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.moderationDictionary = moderationDictionary;
        this.executor = executor;
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

    @Async("executor")
    public void moderatePostsContent() {
        List<Post> unverifiedPosts = postRepository.findReadyToVerified();

        for (int i = 0; i < unverifiedPosts.size(); i += sublistLength) {
            List<Post> subList = unverifiedPosts.subList(i, (int) Math.min(unverifiedPosts.size(), i + sublistLength));

            Map<Long, String> postsContent = new HashMap<>();
            subList.forEach(post -> postsContent.put(post.getId(), post.getContent()));

            CompletableFuture<Map<Long, Boolean>> verifiedPosts =
                    CompletableFuture.supplyAsync(() -> moderationDictionary.searchSwearWords(postsContent), executor);

            verifiedPosts.thenAccept(map -> {
                subList.forEach(post -> {
                    post.setVerified(map.get(post.getId()));
                    post.setVerifiedDate(LocalDateTime.now());

                    postRepository.save(post);
                });
            });
        }
    }
}
