package faang.school.postservice.client;

import faang.school.postservice.client.handler.FeignErrorHandler;
import faang.school.postservice.controller.handler.ErrorResponse;
import faang.school.postservice.mapper.ErrorResponseMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FeignClientErrorDecoder implements ErrorDecoder {

    private final List<FeignErrorHandler> handlers;
    private final ErrorResponseMapper errorResponseMapper;
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String error = errorResponseMapper
                .fromFeignResponse(response)
                .map(ErrorResponse::getError)
                .orElse("Unknown error");

        return handlers.stream()
                .filter(handler -> handler.supports(methodKey, status))
                .findFirst()
                .map(handler -> handler.toException(error))
                .orElseGet(() -> defaultDecoder.decode(methodKey, response));
    }
}

