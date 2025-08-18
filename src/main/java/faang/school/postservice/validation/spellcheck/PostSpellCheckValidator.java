package faang.school.postservice.validation.spellcheck;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostSpellCheckValidator {

    public boolean hasValidText(Post post) {
        String content = post.getContent();
        boolean hasText = content != null && !content.isBlank();
        if (!hasText) {
            log.warn("Skipping post ID {}: empty content", post.getId());
        }
        return hasText;
    }

    public boolean isContentChanged(Post post, String correctedContent) {
        boolean changed = !correctedContent.equals(post.getContent());
        if (!changed) {
            log.debug("Post ID {} doesn't need correction", post.getId());
        }
        return changed;
    }
}
