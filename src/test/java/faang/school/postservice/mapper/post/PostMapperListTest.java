package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.IntStream;

import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDtosForMapping;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostsForMapping;
import static org.assertj.core.api.Assertions.assertThat;

class PostMapperListTest {
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    private final PostMapperList postMapperList = new PostMapperList(postMapper);

    private final List<Post> posts = buildPostsForMapping();
    private final List<PostCacheDto> postCacheDtos = buildPostCacheDtosForMapping();

    @Test
    void testMapToPostCacheDtosSuccessful() {
        List<PostCacheDto> result = postMapperList.mapToPostCacheDtos(posts);
        IntStream.range(0, result.size())
                .forEach(i -> assertThat(result.get(i))
                        .usingRecursiveComparison()
                        .isEqualTo(postCacheDtos.get(i)));
    }
}