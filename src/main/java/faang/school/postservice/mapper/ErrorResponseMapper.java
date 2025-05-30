package faang.school.postservice.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.controller.handler.ErrorResponse;
import feign.Response;
import feign.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorResponseMapper {

    private final ObjectMapper objectMapper;

    public Optional<ErrorResponse> fromFeignResponse(Response response) {
        if (response.body() == null) {
            return Optional.empty();
        }
        try {
            String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
            return Optional.ofNullable(error);
        } catch (IOException e) {
            log.warn("Failed to parse error response body", e);
            return Optional.empty();
        }
    }
}
