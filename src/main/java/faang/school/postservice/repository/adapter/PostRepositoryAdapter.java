package faang.school.postservice.repository.adapter;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
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
            log.error("There is no post with ID {}", id);
            return new EntityNotFoundException("There is no post with ID " + id);
        });
    }
}
