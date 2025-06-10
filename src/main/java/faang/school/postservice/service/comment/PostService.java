package faang.school.postservice.service.comment;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }
}