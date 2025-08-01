package faang.school.postservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheKeyValuePreparator {
    private final ObjectMapper objectMapper;

    public String prepareCommentKey(Comment comment) {
        return String.format("Comment-%d", comment.getId());
    }

    public Object prepareCommentAuthorValue(Comment comment, UserDto userDto) {
        try {
            return objectMapper.writeValueAsString(userDto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(String.format("Prepare Author Value for Comment id: %d failed", comment.getId()),e);
        }
    }
}