package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public class FeignClientException extends RuntimeException {
    private final int status;
    private final String path;

    public FeignClientException(int status, String path, String message) {
        super(message);
        this.status = status;
        this.path = path;
    }
}
