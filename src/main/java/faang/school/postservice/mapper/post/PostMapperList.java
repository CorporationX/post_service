package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PostMapperList {
    private final PostMapper postMapper;

    public List<PostCacheDto> mapToPostCacheDtos(List<Post> posts) {
        return posts.stream()
                .map(postMapper::toPostCacheDto)
                .toList();
    }
}
