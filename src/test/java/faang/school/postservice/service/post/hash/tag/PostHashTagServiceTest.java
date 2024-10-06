package faang.school.postservice.service.post.hash.tag;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static faang.school.postservice.util.post.PostCacheFabric.buildPost;
import static org.assertj.core.api.Assertions.assertThat;

class PostHashTagServiceTest {
    private static final String HASH_TAG = "java";
    private static final String HASH_TAG_JSON = "[\"java\"]";

    private final PostHashTagParser postHashTagParser = new PostHashTagParser();

    private final String defaultPostContent = "Test post content #java #sql#redis";
    private final List<String> defaultHashTags = List.of("java", "sql", "redis");
    private final List<String> defaultUpdatedHashTags = List.of("sql", "redis", "jedis");

    @Test
    @DisplayName("Given new un hash-tags Post and update all hash-tags")
    void testUpdateHashTagsSuccessful() {
        Post post = buildPost(defaultPostContent, new ArrayList<>());
        Post postExpected = buildPost(defaultPostContent, defaultHashTags);
        postHashTagParser.updateHashTags(post);

        assertThat(post)
                .usingRecursiveComparison()
                .isEqualTo(postExpected);
    }

    @Test
    @DisplayName("Given primal and updated hash-tags and return new hash-tags")
    void testGetNewHashTagsSuccessful() {
        assertThat(postHashTagParser.getNewHashTags(defaultHashTags, defaultUpdatedHashTags))
                .isEqualTo(List.of(defaultUpdatedHashTags.get(defaultUpdatedHashTags.size() - 1)));
    }

    @Test
    @DisplayName("Given primal and updated hash-tags and return deleted hash-tags")
    void testGetDeletedHashTagsSuccessful() {
        assertThat(postHashTagParser.getDeletedHashTags(defaultHashTags, defaultUpdatedHashTags))
                .isEqualTo(List.of(defaultHashTags.get(0)));
    }

    @Test
    @DisplayName("Given hash-tag as simple string and return hash-tag like json")
    void testConvertTagToJson() {
        assertThat(postHashTagParser.convertTagToJson(HASH_TAG))
                .isEqualTo(HASH_TAG_JSON);
    }
}