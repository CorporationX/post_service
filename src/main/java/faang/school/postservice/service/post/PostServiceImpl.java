package faang.school.postservice.service.post;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
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

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

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
}