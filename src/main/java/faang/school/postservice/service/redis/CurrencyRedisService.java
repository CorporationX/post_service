package faang.school.postservice.service.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class CurrencyRedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveCurrencyRates(Map<String, Object> rates) {
        redisTemplate.opsForValue().set("currencyRates", rates);
    }

    public Map<String, Object> getCurrencyRates() {
        return (Map<String, Object>) redisTemplate.opsForValue().get("currencyRates");
    }
}
