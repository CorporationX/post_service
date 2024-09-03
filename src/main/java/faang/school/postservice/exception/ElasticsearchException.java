package faang.school.postservice.exception;

public class ElasticsearchException extends RuntimeException {
    public ElasticsearchException(String message) {
        super(message);
    }

}