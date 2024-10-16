package faang.school.postservice.service.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.redis.CurrencyRedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class CurrencyRedisServiceTest {
    @Mock
    private RedisTemplate<String, Object> customRedisTemplateObject;

    @InjectMocks
    private CurrencyRedisService currencyRedisService;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSaveCurrencyRates() throws JsonProcessingException {

        String key = "currencyRates";
        Map<String, Object> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);

        when(customRedisTemplateObject.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(any(), any());
        when(valueOperations.get(key)).thenReturn("EUR=0.85, USD=1.0");
        String ratesJson = objectMapper.writeValueAsString(rates);
        when(customRedisTemplateObject.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(ratesJson);

        currencyRedisService.saveCurrencyRates(key, rates);

        verify(valueOperations).set(key, objectMapper.writeValueAsString(rates));
    }

    @Test
    void testGetCurrencyRates_ReturnsCorrectRates() throws JsonProcessingException {
        String key = "currencyRates";
        Map<String, Object> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);

        String ratesJson = objectMapper.writeValueAsString(rates);

        when(customRedisTemplateObject.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(ratesJson);

        Map<String, Object> retrievedRates = currencyRedisService.getCurrencyRates(key);

        assertEquals(rates, retrievedRates);
    }

    @Test
    void testGetCurrencyRates_ReturnsEmptyMap_WhenJsonProcessingException() throws JsonProcessingException {
        String key = "currencyRates";

        when(customRedisTemplateObject.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn("invalid json");

        Map<String, Object> retrievedRates = currencyRedisService.getCurrencyRates(key);

        assertEquals(Collections.emptyMap(), retrievedRates);
    }
}
