package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.album.AlbumDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlbumCreatedEventPublisher extends AbstractEventPublisher<AlbumDto> {
    @Value("${spring.data.redis.channels.album.name}")
    private String albumTopic;

    public AlbumCreatedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                      ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(AlbumDto albumDto) {
        send(albumTopic, albumDto);
    }
}
