package faang.school.postservice.model.redis;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewsFeedRedisEntity implements Serializable {
    private String userId;
    private String postId;
    private Long timeStamp;
}
