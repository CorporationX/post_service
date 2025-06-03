package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.CommentEvent;
import org.springframework.stereotype.Service;

@Service
public interface RedisCommentService {
    void saveComment(CommentEvent event);
}
