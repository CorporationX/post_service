package faang.school.postservice.dictionary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Component
@PropertySource("classpath:moderation.properties")
public class ModerationDictionary {


    @Value("${moderation.file.path}")
    private String filePath;

    public void checkComment() throws FileNotFoundException {
        Set<String> forbiddenWords = new HashSet<>();
        assert filePath != null;
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            forbiddenWords.add(scanner.nextLine().trim().toLowerCase());
        }
        scanner.close();
    }
}
