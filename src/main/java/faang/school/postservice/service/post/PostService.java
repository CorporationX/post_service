package faang.school.postservice.service.post;

import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post getPost(long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            log.error(ExceptionMessages.POST_NOT_FOUND);
            return new EntityNotFoundException(ExceptionMessages.POST_NOT_FOUND);
        });
    }
}
