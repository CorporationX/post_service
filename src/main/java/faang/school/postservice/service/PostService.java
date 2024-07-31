package faang.school.postservice.service;

import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post getById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Post id=%d not found", id)));
    }
}
