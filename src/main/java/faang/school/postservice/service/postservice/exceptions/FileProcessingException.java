package faang.school.postservice.service.postservice.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileProcessingException extends RuntimeException {
    private final String fileName;

    public FileProcessingException(String message, String fileName, Throwable cause) {
        super(message, cause);
        this.fileName = fileName;
    }


}
