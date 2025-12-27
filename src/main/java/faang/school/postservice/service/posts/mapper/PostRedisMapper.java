package faang.school.postservice.service.posts.mapper;

import faang.school.postservice.model.Post;
import faang.school.postservice.redis.model.RedisPost;
import org.springframework.stereotype.Component;

@Component
public class PostRedisMapper {

    public RedisPost toRedis(Post post) {
        return RedisPost.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .content(post.getContent())
                .build();
    }
}