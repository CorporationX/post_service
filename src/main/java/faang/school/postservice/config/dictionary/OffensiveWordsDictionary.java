package faang.school.postservice.config.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class OffensiveWordsDictionary {

    private final Set<String> offensiveDictionary = new CopyOnWriteArraySet<>();

    public OffensiveWordsDictionary(@Qualifier("offensiveWords") List<String> newWords) {
        addWordsToDictionary(newWords);
    }

    public boolean isWordContainsInDictionary(String word) {
        return offensiveDictionary.contains(word);
    }

    public void addWordsToDictionary(List<String> newWords) {
        offensiveDictionary.addAll(newWords);
    }
}
