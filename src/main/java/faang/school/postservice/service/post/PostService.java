package faang.school.postservice.service.post;

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

    public Post findById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElseThrow(
                ()-> new EntityNotFoundException("Post service. Post not found. id: " + id));
    }
}
