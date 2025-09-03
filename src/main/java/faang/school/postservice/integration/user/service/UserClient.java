package faang.school.postservice.integration.user.service;

import faang.school.postservice.integration.user.config.UserClientProperties;
import faang.school.postservice.integration.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@EnableConfigurationProperties(UserClientProperties.class)
@RequiredArgsConstructor
public class UserClient implements UserServiceClient {

    private final UserClientProperties properties;

    @Qualifier("userWebClient")
    private final WebClient webClient;

    @Override
    public UserResponseDto getUser(long id) {
        ResponseEntity<UserResponseDto> responseEntity = webClient
                .get()
                .uri(u -> {
                    return u.path(properties.getUserUrl() + "/" + id)
                            .build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(UserResponseDto.class)
                .onErrorMap(e -> {
                    try {
                        return new WebClientRequestException(e, HttpMethod.GET, new URI(""), HttpHeaders.EMPTY);
                    } catch (URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .onErrorMap(e -> new WebClientResponseException(e.getMessage(),
                                HttpStatusCode.valueOf(426).value(),
                                "Произошла ошибка при обработке ответа",
                                null, null, null
                        )
                )
                .block();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new ResponseStatusException(responseEntity.getStatusCode(), responseEntity.getBody().toString());
        }
    }
}
