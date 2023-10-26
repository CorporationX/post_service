//package faang.school.postservice.model.redis;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Version;
//import org.springframework.data.redis.core.RedisHash;
//
//import java.time.LocalDateTime;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@RedisHash(value = "Comment", timeToLive = 86400)
//public class RedisComment {
//    @Id
//    private Long commentId;
//    private Long authorId;
//    private Long postId;
//    private Long commentLikes;
//    @Version
//    private Long version;
//    private LocalDateTime publishedAt;
//}
