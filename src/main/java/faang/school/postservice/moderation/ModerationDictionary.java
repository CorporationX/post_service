package faang.school.postservice.moderation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModerationDictionary {

    private final Dictionary dictionary;

    public void searchSwearWords(List<? extends Verifyible> verifyibles) {
        verifyibles.forEach(entity -> {
            boolean containsSwearWord = dictionary.getDictionary().stream()
                    .anyMatch(word -> entity.getContentText().contains(word));

            entity.setVerificationValue(!containsSwearWord);
            entity.initVerifiedDate();
        });
    }
}
