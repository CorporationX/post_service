package faang.school.postservice.integration.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.integration.IntegrationTestBase;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = "test")
@DirtiesContext
public class LikeEventIntegrationTest extends IntegrationTestBase {

    private static final Long USER_ID = 1L;
    private static final int USER_SERVICE_PORT = 9080;

    @Autowired
    private LikeService likeService;

    private static WireMockServer wireMockServer;

    private static final Long POST_ID = 1L;
    private static final String author = """
            {
                "id": 1,
                "username": "JohnDoe",
                "email": "johndoe@example.com",
                "preference": "EMAIL"
            }
            """;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(USER_SERVICE_PORT));
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.get("/users/1")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(author)));
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void shouldSendKafkaEvent() {
        LikeDto likeDto = LikeDto.builder()
                .userId(USER_ID)
                .postId(POST_ID)
                .build();

        likeService.addLikeToPost(POST_ID, likeDto);
    }
}
