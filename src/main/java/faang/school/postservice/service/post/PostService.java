package faang.school.postservice.service.post;

import faang.school.postservice.corrector.ContentCorrector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final ContentCorrector contentCorrector;
    public void correctionTextInPost(Long postId) {
        contentCorrector.spellCheckPostById(postId);
    }
}
