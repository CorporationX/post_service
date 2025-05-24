package faang.school.postservice.client;

import faang.school.postservice.exception.project_service_client.ProjectNotFoundException;
import faang.school.postservice.exception.user_service_client.UserNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.resolve(response.status());
        if (status == HttpStatus.NOT_FOUND) {
            if (methodKey.contains("UserServiceClient#getUser")) {
                return new UserNotFoundException("User not found");
            } else if (methodKey.contains("ProjectServiceClient#getProject")) {
                return new ProjectNotFoundException("Project not found");
            }
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
