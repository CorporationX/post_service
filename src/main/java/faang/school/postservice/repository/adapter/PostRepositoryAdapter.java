package faang.school.postservice.repository.adapter;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter {
    private final PostRepository postRepository;

    public Post getById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
    }

    public Post getByIdWithLikes(long id) {
        return postRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
    }
}
