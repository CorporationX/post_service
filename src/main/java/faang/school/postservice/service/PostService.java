package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post findById(long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            log.info("Post with id {} does not exist", id);
            return new EntityNotFoundException("Post with id " + id + " does not exist");
        });
    }
}