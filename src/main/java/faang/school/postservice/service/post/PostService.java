package faang.school.postservice.service.post;

import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    public Post getPostById(long postId){
        return postRepository.findById(postId)
                .orElseThrow(()->new DataValidationException("This post was not found"));
    }
}
