package faang.school.postservice.integration.project;

import faang.school.postservice.integration.project.dto.ProjectResponseDto;
import faang.school.postservice.integration.project.service.ProjectServiceClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectClient implements ProjectServiceClient {

    @Override
    public ProjectResponseDto getProject(long id) {

        ResponseEntity<ProjectResponseDto> responseEntity = WebClient.builder().baseUrl("").build()
                .get()
                .uri(u -> {
                    return u.path("")
                            .queryParam("id", id)
                            .build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(ProjectResponseDto.class)
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
