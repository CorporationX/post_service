package faang.school.postservice.client;

import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.exception.IntegrationException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        String message;
        try (InputStream bodyIs = response.body().asInputStream();
             InputStreamReader reader = new InputStreamReader(bodyIs, StandardCharsets.UTF_8)) {
            message = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }

        if (response.status() == 404) {
            return new DataNotFoundException(message);
        }
        return new IntegrationException(message);
    }
}
