package faang.school.postservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModerationDictionary {

    private static final String FILE_SWEARWORDS = "files/list-of-swearwords-and-offensive-gestures.csv";
    private static final String FILE_NOT_FOUND = "File " + FILE_SWEARWORDS + " not found";

    private final Set<String> dictionary;

    public ModerationDictionary() throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(FILE_SWEARWORDS);
        if (resource != null) {
            dictionary = parseCsv(resource.getFile());
        } else {
            log.error(FILE_NOT_FOUND);
            throw new FileNotFoundException(FILE_NOT_FOUND);
        }
    }

    public boolean checkString(String string) {
        for (String word : dictionary) {
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
