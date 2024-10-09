package faang.school.postservice.moderation;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ModerationDictionary {

    private final Dictionary dictionary;

    public List<Post> searchSwearWords(List<Post> unverifiedPosts) {
        unverifiedPosts.forEach(post -> {
            boolean containsSwearWord = dictionary.getDictionary().stream()
                    .anyMatch(word -> post.getContent().contains(word));

            post.setVerified(!containsSwearWord);
            post.setVerifiedDate(LocalDateTime.now());
        });

        return unverifiedPosts;
    }
}
