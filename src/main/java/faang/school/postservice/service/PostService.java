package faang.school.postservice.service;

import faang.school.postservice.exceptions.DataNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    public Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException(String
                        .format("Post with id:%d doesn't exist", postId)));
    }
}
