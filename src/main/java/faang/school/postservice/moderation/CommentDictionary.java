package faang.school.postservice.moderation;

import faang.school.postservice.exception.ModerationDictionaryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class CommentDictionary {
    private Set<String> bannedWords;

    public CommentDictionary() {
        this.bannedWords = loadBannedWords();
    }

    public boolean containsBannedWord(String text) {
        String lowerText = text.toLowerCase();
        return bannedWords.stream()
                .anyMatch(lowerText::contains);
    }

    private Set<String> loadBannedWords() {
        try {
            InputStream inputStream = new ClassPathResource("comment_control/bad_words.xml").getInputStream();
            return parseXmlDictionary(inputStream);
        } catch (Exception e) {
            log.error("Error loading banned words dictionary", e);
            throw new ModerationDictionaryException("Error loading banned words", e);
        }
    }

    private Set<String> parseXmlDictionary(InputStream inputStream) throws Exception {
        Set<String> words = new HashSet<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);

        NodeList wordNodes = doc.getElementsByTagName("word");
        for (int i = 0; i < wordNodes.getLength(); i++) {
            Element wordElement = (Element) wordNodes.item(i);
            String word = wordElement.getTextContent().trim().toLowerCase();
            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        log.info("Loaded {} banned words from XML dictionary", words.size());
        return words;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void reloadBannedWords() {
        Set<String> previousWords = new HashSet<>(this.bannedWords);
        this.bannedWords = loadBannedWords();

        if (bannedWords.equals(previousWords)) {
            log.info("Banned words dictionary reloaded, no changes detected");
        } else {
            log.info("Banned words dictionary reloaded. New word count: {}", bannedWords.size());
        }
    }
}