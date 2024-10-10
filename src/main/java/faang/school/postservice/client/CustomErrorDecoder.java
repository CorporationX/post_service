package faang.school.postservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.FeignClientException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.body() == null) {
            return new FeignClientException(response.status(), methodKey, "Empty response body");
        }
        String responseBody = "";

        try (InputStream bodyIs = response.body().asInputStream()) {
            responseBody = new String(bodyIs.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new FeignClientException(response.status(), methodKey, "Error reading response body");
        }

        try {
            JsonNode rootNode = mapper.readTree(responseBody);
            int status = rootNode.path("status").asInt();
            String message = rootNode.path("message").asText();
            String path = rootNode.path("path").asText();

            return new FeignClientException(status, path, message);
        } catch (IOException e) {
            return new FeignClientException(response.status(), methodKey, "Failed to parse error response: " + responseBody);
        }
    }
}

