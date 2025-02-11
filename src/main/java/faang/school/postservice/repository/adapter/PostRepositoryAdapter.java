package faang.school.postservice.repository.adapter;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter {
    private final PostRepository postRepository;

    public Post getById(long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            log.error("Post with ID {} not found", id);
            return new EntityNotFoundException("Post with ID {} not found" + id);
        });
    }
}
