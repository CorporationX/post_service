package faang.school.postservice.redis.mapper;

import faang.school.postservice.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class PostCacheMapperTest {
    private final PostCacheMapper mapper = Mappers.getMapper(PostCacheMapper.class);

    @Test
    void givenPostDtoWhenToPostCacheThenReturn() {
        // given - precondition
        var postDto = TestDataFactory.createPostDto();
        var expectedResult = TestDataFactory.createPostCache();

        // when - action
        var actualResult = mapper.toPostCache(postDto);

        // then - verify the output
        assertThat(actualResult).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}