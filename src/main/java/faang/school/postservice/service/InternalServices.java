package faang.school.postservice.service;

import faang.school.postservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class InternalServices {
    private final RestTemplate restTemplate;

    @Value("${project-protocol}")
    private String protocol;

    @Value("${user-service.host}")
    private String userServiceHost;

    @Value("${user-service.port}")
    private int userServicePort;

    @Value("${user-service.endpoints.getById}")
    private String userServiceEndpointGetById;

    @Value("${project-service.host}")
    private String projectServiceHost;

    @Value("${project-service.port}")
    private int projectServicePort;

    @Value("${project-service.endpoints.getById}")
    private String projectServiceEndpoint;

    public boolean userExists(Long userId) {
        String url = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(userServiceHost)
                .port(userServicePort)
                .path(userServiceEndpointGetById)
                .buildAndExpand(userId)
                .toUriString();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK && StringUtils.hasText(response.getBody());
        } catch (Exception e) {
            throw new ExternalServiceException("Error checking if user exists", e);
        }
    }

    public boolean projectExists(Long projectId) {
        String url = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(projectServiceHost)
                .port(projectServicePort)
                .path(projectServiceEndpoint)
                .buildAndExpand(projectId)
                .toUriString();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK && StringUtils.hasText(response.getBody());
        } catch (Exception e) {
            throw new ExternalServiceException("Error checking if project exists", e);
        }
    }
}
