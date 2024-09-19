package faang.school.postservice.post.hash.tag;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostHashTagService {
    private final PostRepository postRepository;

    public List<String> parsePostByHashTa(Post post) {
        return null;
    }
}
