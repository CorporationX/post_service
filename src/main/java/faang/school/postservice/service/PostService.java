package faang.school.postservice.service;

import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with id " + postId + " was not found!"));
    }
}
