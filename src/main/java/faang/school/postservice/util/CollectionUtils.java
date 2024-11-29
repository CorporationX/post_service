package faang.school.postservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CollectionUtils {

    public <T> void replaceNullsWith(List<T> targetList, List<T> replacements) {
        for (int i = 0, replacementIndex = 0; i < targetList.size(); i++) {
            if (targetList.get(i) == null) {
                T replacementValue = replacements.get(replacementIndex++);
                targetList.set(i, replacementValue);
            }
        }
    }
}
