package faang.school.postservice.moderation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
@NoArgsConstructor
@PropertySource("classpath:/moderation_dictionary.yaml")
public class ModerationDictionary implements Moderator<String> {
    @Value("#{'${badWords}'.split(',')}")
    private Set<String> badWords;


    @Override
    public boolean inspect(String moderationObject) {
        Set<String> text = new HashSet<>(Arrays.asList(moderationObject.toLowerCase().split("\\s+")));
        return text.stream()
                .anyMatch(badWords::contains);
    }
}