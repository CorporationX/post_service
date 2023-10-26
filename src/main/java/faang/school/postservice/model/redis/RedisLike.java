//package faang.school.postservice.model.redis;
//
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Version;
//import org.springframework.data.redis.core.RedisHash;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@RedisHash(value = "Like", timeToLive = 86400)
//public class RedisLike implements Serializable {
//    @Id
//    private Long likeId;
//    private Long userId;
//    private Long postId;
//    @Version
//    private Long version;
//    private LocalDateTime publishedAt;
//}
