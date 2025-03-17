package faang.school.postservice.repository.adapter;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter {
    private final PostRepository postRepository;

    public Post getByIdWithLikes(long id) {
        return postRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
    }
}
