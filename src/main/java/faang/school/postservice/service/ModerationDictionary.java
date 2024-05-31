package faang.school.postservice.service;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ModerationDictionary {

    private static final String FILE_SWEARWORDS = "files/list-of-swearwords-and-offensive-gestures.csv";

    private final Set<String> DICTIONARY;

    public ModerationDictionary() throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(FILE_SWEARWORDS);
        if (resource != null) {
            DICTIONARY = parseCsv(resource.getFile());
        } else {
            throw new FileNotFoundException("File " + FILE_SWEARWORDS + " not found");
        }
    }

    public boolean checkString(String string) {
        for (String word : DICTIONARY) {
            if (string.toLowerCase().contains(word.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    private Set<String> parseCsv(String csvFile) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvFile))) {
            return reader.lines()
                    .map(line -> line.split(",")[1])
                    .skip(1)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
