package faang.school.postservice.moderation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ModerationDictionary {

    private final Dictionary dictionary;

    public void searchSwearWords(List<? extends Verifiable> verifyibles) {
        verifyibles.forEach(entity -> {
            boolean containsSwearWord = dictionary.getDictionary().stream()
                    .anyMatch(word -> entity.getContent().contains(word));

            entity.setVerified(!containsSwearWord);
            entity.setVerifiedDate(LocalDateTime.now());
        });
    }
}
