package faang.school.postservice.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Component
public interface FileTypeAndSizeValidation {
    void validate(List<MultipartFile> files,
                  Map<String, Long> typeSpecificSizeLimits);
}
