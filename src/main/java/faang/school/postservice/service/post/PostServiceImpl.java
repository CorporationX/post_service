package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Optional<Post> getPost(long postId) {
        return postRepository.findById(postId);
    }
}
