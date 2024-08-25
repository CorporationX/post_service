package faang.school.postservice.mapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Data
@Component
public class PostContextMapper {
    private Map<Long, Long> countLikeEveryonePost = new HashMap<>();

    public long getCountLike(Long postId) {
        return countLikeEveryonePost.getOrDefault(postId, 0L);
    }
}
