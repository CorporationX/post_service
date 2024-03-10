package faang.school.postservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceValidator {
    @Value("${post.content_to_post.max_amount}")
    private int maxAmountFiles;
    @Value("${post.content_to_post.max_size}")
    private int maxFileSize;

    public void validateFileSize(long fileSize) {
        if (fileSize > maxFileSize) {
            throw new IllegalArgumentException("Size of file must be equals or less than 5 mb");
        }
    }

    public void validateFilesAmount(int existFilesAmount, int newFilesAmount) {
        if (existFilesAmount + newFilesAmount > maxAmountFiles) {
            String exceptionMsg = String.format("You can upload only 10 files or less. Exist files = %s. New files = %s"
                    , existFilesAmount, newFilesAmount);
            throw new IllegalArgumentException(exceptionMsg);
        }
    }
}
