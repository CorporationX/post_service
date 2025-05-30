package faang.school.postservice.client.handler;

public interface FeignErrorHandler {

    boolean supports(String methodKey, int status);

    Exception toException(String message);
}
