package faang.school.postservice.messages.redis.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.JsonSerializeException;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserBanPublisherImpl implements UserBanPublisher {
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> template;
    private final ChannelTopic userBanTopic;
    @Value("${comment.count-ban-size}")
    private int banSize;

    @Override
    public void banUserComment() {
        List<Long> verifiedUsers = commentRepository.findAllBanUser(banSize);
        if (!verifiedUsers.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json;
            try {
                json = objectMapper.writeValueAsString(verifiedUsers);
            } catch (JsonProcessingException e) {
                throw new JsonSerializeException("Error to serialize");
            }
            template.convertAndSend(userBanTopic.getTopic(), json);
        }
    }
}