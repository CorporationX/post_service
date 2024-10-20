package faang.school.postservice.scheduler.post.moderation;

import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Emit;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AhoCorasickContentChecker {
    private final Trie trie;

    public AhoCorasickContentChecker(ModerationDictionary moderationDictionary) {
        Trie.TrieBuilder builder = Trie.builder()
                .onlyWholeWords()
                .caseInsensitive();

        for (String word : moderationDictionary.getBadWords()) {
            builder.addKeyword(word);
        }

        this.trie = builder.build();
    }

    public boolean containsBadContent(String text) {
        Collection<Emit> emits = trie.parseText(text);
        return !emits.isEmpty();
    }
}

