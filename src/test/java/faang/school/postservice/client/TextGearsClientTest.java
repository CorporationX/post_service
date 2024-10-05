package faang.school.postservice.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import faang.school.postservice.config.TextGearsProperties;
import faang.school.postservice.dto.text.gears.TextGearsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WireMockTest
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock
class TextGearsClientTest {

    @Autowired
    private TextGearsClient textGearsClient;

    @SpyBean
    private TextGearsProperties properties;

    @Test
    void testCorrectText() {
        String text = "Some text to correct", correctedText = "Corrected text";
        String jsonResponse = "{\"status\": true, \"response\": {\"corrected\": \"%s\"}}".formatted(correctedText);

        stubFor(post(urlPathEqualTo(properties.getCorrect()))
                .withQueryParam("text", equalTo(text))
                .withQueryParam("key", equalTo(properties.getApiKey()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withStatus(HttpStatus.OK.value())
                        .withBody(jsonResponse)
                ));

        TextGearsResponse result = textGearsClient.correctText(text);

        assertNotNull(result);
        assertTrue(result.getStatus());
        assertEquals(correctedText, result.getResponse().getCorrected());
    }
}
