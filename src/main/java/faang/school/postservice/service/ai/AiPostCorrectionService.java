package faang.school.postservice.service.ai;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPostCorrectionService {

    private final AiTextCorrectionService aiTextCorrectionService;
    private final PostRepository postRepository;

    @Transactional
    public void correctPost(Post post) {
        try {
            String correctedText = aiTextCorrectionService.correct(post.getContent());

            post.setContent(correctedText);
            post.setAiEdited(true);
            postRepository.save(post);
        } catch (Exception e) {
            log.error("Error editing post id={} - {}", post.getId(), e.getMessage(), e);
        }
    }
}
