package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Optional<Post> getPostById(Long postId) {
        return Optional.ofNullable(postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found")));
    }

    public void updatePost(Post post) {
        postRepository.save(post);
    }
}
