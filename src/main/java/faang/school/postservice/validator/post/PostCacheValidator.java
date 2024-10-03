package faang.school.postservice.validator.post;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostCacheValidator {

    public boolean validateComments(List<Object> range, int maxSize) {
        return range == null || range.size() != maxSize;
    }
}
