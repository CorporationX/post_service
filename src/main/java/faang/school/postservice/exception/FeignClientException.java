package faang.school.postservice.exception;

public class FeignClientException extends RuntimeException {
    public FeignClientException(String message) {
        super(message);
    }
}
