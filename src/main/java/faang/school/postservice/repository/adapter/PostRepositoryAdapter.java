package faang.school.postservice.repository.adapter;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter {
    private final PostRepository postRepository;

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> findById(long postId) {
        return postRepository.findById(postId);
    }

    public List<Post> findByAuthorId(long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public List<Post> findByProjectId(long projectId) {
        return postRepository.findByProjectId(projectId);
    }

    public Post getById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no post with ID " + id));
    }
}
