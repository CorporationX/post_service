package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.JsonSerializeException;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserBanPublisherImpl implements UserBanPublisher {
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> template;
    private final ChannelTopic userBanTopic;
    @Value("${comment.batch-size}")
    private int BATCH_SIZE;

    @Override
    public void banUserComment() {
        int page = 0;
        Page<Long> commentPage;
        Map<Long, Long> verifiedUsers;
        do {
            commentPage = commentRepository.findAllBanUser(PageRequest.of(page, BATCH_SIZE));
            verifiedUsers = commentPage.getContent().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            page++;
        } while (commentPage.hasNext());
        List<Long> usersBan = verifiedUsers.entrySet().stream()
                .filter(entry -> entry.getValue() >= 5)
                .map(Map.Entry::getKey)
                .toList();
        if (!usersBan.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json;
            try {
                json = objectMapper.writeValueAsString(usersBan);
            } catch (JsonProcessingException e) {
                throw new JsonSerializeException("Error to serialize");
            }
            template.convertAndSend(userBanTopic.getTopic(), json);
        }
    }
}