package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.model.Feed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedMapper {

    private final PostCacheMapper postCacheMapper;

    public FeedDto toDto(Feed feed) {
        List<PostDto> postDtos = postCacheMapper.toDto(feed.getPosts());
        return new FeedDto(feed.getUserId(), postDtos);
    }

}
