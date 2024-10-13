package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyRedisService {
    private final RedisTemplate<String, Object> customRedisTemplateObject;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);


    public void saveCurrencyRates(String key, Map<String, Object> rates) {
        log.info("Start saving currency rates to Redis: {}", rates);
        try {
            String ratesJson = objectMapper.writeValueAsString(rates);
            customRedisTemplateObject.opsForValue().set(key, ratesJson);
            log.info("Currency rates saved to Redis: {}", ratesJson);
        } catch (JsonProcessingException e) {
            log.error("Error while saving currency rates to Redis", e);
        }
        log.info("End saving currency rates to Redis: {}", getCurrencyRates(key));
    }

    public Map<String, Object> getCurrencyRates(String key) {
        String ratesJson = (String) customRedisTemplateObject.opsForValue().get(key);
        try {
            return objectMapper.readValue(ratesJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error while getting currency rates from Redis", e);
            return Collections.emptyMap();
        }
    }
}
