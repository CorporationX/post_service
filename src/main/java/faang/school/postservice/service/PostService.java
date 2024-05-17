package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static faang.school.postservice.exception.MessageForException.NO_POST_IN_DB;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;

    public Post getPostById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new DataValidationException(NO_POST_IN_DB.getMessage()));
    }
}
