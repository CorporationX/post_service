package faang.school.postservice.service;

import faang.school.postservice.client.web.YandexSpellerClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YandexSpellCorrectorServiceTest {

    @Mock
    private YandexSpellerClient yandexSpellerClient;

    @InjectMocks
    private YandexSpellCorrectorService spellCorrectorService;

    @Test
    void testGetCorrectedText_WithDifferentInputText() {
        String[][] testCases = {
                {"Это тестовое предложение.", "Это тестовое предложение."},
                {"Тстове предложене с ошыбками.", "Тестовое предложение с ошибками."},
                {"Превет!", "Привет!"}
        };

        for (String[] testCase : testCases) {
            String inputText = testCase[0];
            String expectedOutput = testCase[1];

            when(yandexSpellerClient.checkText(inputText)).thenReturn(getMockSpellResponse());

            String result = spellCorrectorService.getCorrectedText(inputText);

            assertEquals(expectedOutput, result);
        }
    }

    private String getMockSpellResponse() {
        return "["
                + "{\"s\":[\"Тестовое\"],\"word\":\"Тстове\",\"pos\":0,\"row\":1,\"col\":1,\"len\":7,\"code\":1},"
                + "{\"s\":[\"предложение\"],\"word\":\"предложене\",\"pos\":8,\"row\":1,\"col\":10,\"len\":10,\"code\":1},"
                + "{\"s\":[\"ошибками\"],\"word\":\"ошыбками\",\"pos\":21,\"row\":1,\"col\":23,\"len\":8,\"code\":1},"
                + "{\"s\":[\"Привет\"],\"word\":\"Превет\",\"pos\":0,\"row\":1,\"col\":1,\"len\":6,\"code\":1}"
                + "]";
    }
}
