package faang.school.postservice.service.ai;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostTransactionalService {

    private final PostRepository postRepository;

    @Transactional
    public List<Post> loadPostsForAiEditing() {
        return postRepository.findAllForAiEditingWithLock();
    }
}
