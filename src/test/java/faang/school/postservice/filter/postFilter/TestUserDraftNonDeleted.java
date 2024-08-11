package faang.school.postservice.filter.postFilter;

import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.filter.post.filterImpl.PostFilterUserDraftNonDeleted;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUserDraftNonDeleted {
    private PostFilterDto filters;
    private List<Post> posts;
    private PostFilter filter;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        boolean isDeleted = false;
        boolean isPublished = false;
        Post postFirst = createPost(userId, !isDeleted, isPublished);
        Post postSecond = createPost(userId, isDeleted, !isPublished);
        Post postThird = createPost(userId, !isDeleted, !isPublished);
        Post postForth = createPost(userId++, isDeleted, isPublished);
        Post postFifth = createPost(userId, isDeleted, isPublished);
        posts = List.of(postFirst, postSecond, postThird, postForth, postFifth);
        filters = new PostFilterDto(1L, null, isDeleted, isPublished);
        filter = new PostFilterUserDraftNonDeleted();
    }

    @Test
    void testIsApplicable() {
        assertTrue(filter.isApplicable(filters));
    }

    @Test
    void testByUser() {
        // given
        Post expPost = posts.get(3);
        int expSize = 1;

        // when
        List<Post> filteredPosts = posts.stream()
                .filter(p -> filter.test(p, filters))
                .toList();

        // then
        assertEquals(expSize, filteredPosts.size());
        assertEquals(expPost, filteredPosts.get(0));
    }

    private Post createPost(Long userId, boolean isDeleted, boolean isPublished) {
        Post post = new Post();
        post.setAuthorId(userId);
        post.setDeleted(isDeleted);
        post.setPublished(isPublished);
        return post;
    }
}
