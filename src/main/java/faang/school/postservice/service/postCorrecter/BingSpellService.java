package faang.school.postservice.service.postCorrecter;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BingSpellService implements PostCorrecterService {
    private final PostCorrecter postCorrecter;
    private final PostRepository postRepository;

    public void correctUnpublishedPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        for (Post post : posts) {
            try {
                postCorrecter.correctPostText(post).thenAcceptAsync(text -> {
                    post.setContent(text);
                    postRepository.save(post);
                });
            } catch (Exception e) {
                log.error("Failed to process post.", e);
            }
        }
    }
}
