package faang.school.postservice.cache.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewsFeedRedis {
    private Long followerId;
    private List<Long> postIds;
}
