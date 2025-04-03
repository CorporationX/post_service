package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {
    private final String POST_NOT_FOUND = "Post is not found, postId:";
    private final PostRepository postRepository;

    public Post getPost(Long id) {
        Optional<Post> optional = postRepository.findById(id);
        if(optional.isPresent()) {
            return optional.get();
        } else {throw new DataValidationException(POST_NOT_FOUND+id.toString());
        }
    }
}
