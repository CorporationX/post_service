package faang.school.postservice.model.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRedisEntity {
    /**
     * <p>Redis позволяет устанавливать TTL только на весь ключ целиком, а не на отдельные поля
     * внутри хэш-таблицы. То есть можете задать срок хранения для всего ключа, содержащего хэш.
     * При использовании {@code @RedisHash(value = "...")} помимо hashMap'ов для каждого элемента
     * в redis создаётся {@code set} с именем который указан в значении {@code value} внутри которого хранится
     * список всех ключей.</p>
     * <p>Если поставить TTL внутри {@code @RedisHash(value = "...", timeToLive = 3600)}, то удаляться
     * будут только hashMap'ы, а в {@code set} ключ так и остаётся.</p>
     * <p>ПОЭТОМУ ОТКАЗАЛСЯ ОТ ИСПОЛЬЗОВАНИЯ {@code @RedisHash(value = "...")}</p>
     */
    private String postId;
    private String content;
}
