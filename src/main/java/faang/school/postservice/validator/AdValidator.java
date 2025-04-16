package faang.school.postservice.validator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdValidator {

    public void validateAdIds(List<Long> adIds) {
        if (adIds == null || adIds.isEmpty()) {
            throw new IllegalArgumentException("No expired ads found to delete");
        }
    }

    public void validateAdId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ad ID");
        }
    }
}
