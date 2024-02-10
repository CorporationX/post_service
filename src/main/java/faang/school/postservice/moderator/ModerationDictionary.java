package faang.school.postservice.moderator;

import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
public class ModerationDictionary {
    private Set<String> words = new HashSet<>();

    public boolean checkWord(String contentOfComment) {
        return words.stream()
                .anyMatch(word -> contentOfComment.toLowerCase().contains(word.toLowerCase()));
    }
}
