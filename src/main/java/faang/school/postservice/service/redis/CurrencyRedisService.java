package faang.school.postservice.service.redis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyRedisService {
    private final RedisTemplate<String, Object> customRedisTemplateJson;

    public void saveCurrencyRates(String key, Map<String, Object> rates) {
        log.info("Saving currency rates to Redis: {}", rates);
        customRedisTemplateJson.opsForValue().set(key, rates);
    }

    public Map<String, Object> getCurrencyRates() {
        return (Map<String, Object>) customRedisTemplateJson.opsForValue().get("currencyRates");
    }
}
