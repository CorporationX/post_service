package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService{
    private PostRepository postRepository;

    @Override
    public Post findById(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException
                        (String.format("There is no post with id %d", postId)));
    }
}
