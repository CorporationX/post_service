package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.post.PostRedis;
import faang.school.postservice.dto.user.UserNFDto;
import faang.school.postservice.event.PostCommentEvent;

public interface RedisCacheService {
    void savePost(PostRedis post);

    void saveUser(UserNFDto user);

    void incrementPostViews(Long postId);

    void incrementLike(Long postId);

    void addCommentForPost(PostCommentEvent event) throws JsonProcessingException;
}
