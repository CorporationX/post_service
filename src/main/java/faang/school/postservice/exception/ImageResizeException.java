package faang.school.postservice.exception;

public class ImageResizeException extends RuntimeException {
    public ImageResizeException(Throwable cause) {
        super(cause);
    }

    public ImageResizeException(String message) {
        super(message);
    }
}
