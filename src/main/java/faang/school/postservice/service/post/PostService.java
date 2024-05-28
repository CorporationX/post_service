package faang.school.postservice.service.post;


import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post getPostById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
    }
}