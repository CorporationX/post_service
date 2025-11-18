package faang.school.postservice.service.ai;

import faang.school.postservice.client.AIClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPostCorrectionService {

    private final AIClient aiClient;
    private final PostRepository postRepository;

    @Transactional
    public void correctDraftPosts() {
        List<Post> posts = postRepository.findUnpublished();

        for (Post post : posts) {
            try {
                String corrected = aiClient.correctText(post.getContent());
                post.setContent(corrected);
                post.setAiEdited(true);
                postRepository.save(post);

                log.info("Post {} corrected", post.getId());

            } catch (Exception e) {
                log.error("Failed to correct post {}: {}", post.getId(), e.getMessage());
            }
        }
    }
}
