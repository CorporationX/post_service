package faang.school.postservice.moderator;

import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class ModerationDictionary {
    private Set<String> words;

    public boolean checkCommentForInsults(String content) {
        return words.stream()
                .anyMatch(word -> content.toLowerCase().contains(word.toLowerCase()));
    }
}
