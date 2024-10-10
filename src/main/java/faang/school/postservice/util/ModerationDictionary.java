package faang.school.postservice.util;

import faang.school.postservice.model.Post;
import faang.school.postservice.config.ObsceneWordProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ModerationDictionary implements Moderation {

    private final ObsceneWordProperties properties;

    @Override
    public boolean isVerified(Post post) {
        Set<String> words = properties.getWords();
        String content = post.getContent();
        return words.stream().noneMatch(content::contains);
    }
}
