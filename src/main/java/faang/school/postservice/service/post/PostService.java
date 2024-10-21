package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${post.moderation.sublist.length}")
    private Long sublistLength;

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ModerationDictionary moderationDictionary;
    private final ExecutorService executor;

    public PostResponseDto getPost(long postId){
        Post post = findById(postId);
        return postMapper.toResponseDto(post, post.getLikes().size());
    }

    public List<Post> getAllPostsNotPublished() {
        return postRepository.findReadyToPublish();
    }

    public void savePosts(List<Post> posts) {
        postRepository.saveAll(posts);
    }

    public Post findById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        return post.orElseThrow(
                () -> new EntityNotFoundException("Post service. Post not found. id: " + postId));
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

            CompletableFuture<Void> verifiedEntities =
                    CompletableFuture.runAsync(() -> moderationDictionary.searchSwearWords(subList), executor);

            verifiedEntities.thenAccept(result -> postRepository.saveAll(subList));
        }
    }
}
