package faang.school.postservice.service.postservice.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileUploadException extends RuntimeException {
    private final String fileName;

    public FileUploadException(String message, String fileName, Throwable cause) {
        super(message);
        this.fileName = fileName;
    }
}