package faang.school.postservice.filter.postFilter;

import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.filter.post.PostFilterDto;
import faang.school.postservice.filter.post.filterImpl.PostFilterProjectDraftNonDeleted;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestProjectDraftNonDeleted {
    private PostFilterDto filters;
    private List<Post> posts;
    private PostFilter filter;

    @BeforeEach
    void setUp() {
        Long projectId = 1L;
        boolean isDeleted = false;
        boolean isPublished = false;
        Post postFirst = createPost(projectId, !isDeleted, isPublished);
        Post postSecond = createPost(projectId, isDeleted, !isPublished);
        Post postThird = createPost(projectId, !isDeleted, !isPublished);
        Post postForth = createPost(projectId++, isDeleted, isPublished);
        Post postFifth = createPost(projectId, isDeleted, isPublished);
        posts = List.of(postFirst, postSecond, postThird, postForth, postFifth);
        filters = new PostFilterDto(null, 1L, isDeleted, isPublished);
        filter = new PostFilterProjectDraftNonDeleted();
    }

    @Test
    void testIsApplicable() {
        assertTrue(filter.isApplicable(filters));
    }

    @Test
    void testByUser() {
        Post expPost = posts.get(3);
        int expSize = 1;

        List<Post> filteredPosts = posts.stream()
                .filter(p -> filter.test(p, filters))
                .toList();

        assertEquals(expSize, filteredPosts.size());
        assertEquals(expPost, filteredPosts.get(0));
    }

    private Post createPost(Long projectId, boolean isDeleted, boolean isPublished) {
        Post post = new Post();
        post.setProjectId(projectId);
        post.setDeleted(isDeleted);
        post.setPublished(isPublished);
        return post;
    }
}
