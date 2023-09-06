package faang.school.postservice.controller.album;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Objects;

public class UserMock {
    public static void setupUserMockResponse(WireMockServer mockService) {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/users/1"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                (Objects.requireNonNull(UserMock.class
                                        .getClassLoader().getResourceAsStream("getUserResponse.json")))
                                        .toString())));
    }
}
