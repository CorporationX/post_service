package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CacheCommentDto;
import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    private Post post;
    private  List<Comment> comments;
    @InjectMocks
    private PostMapperImpl postMapper;
@BeforeEach
void setUp(){
    post = createTestPost();
    comments = createTestComments(post);
}
    @Test
    void toRedisPostDto_ShouldMapPostToRedisPostDto() {
        post.setComments(comments);

        RedisPostDto redisPostDto = postMapper.toRedisPostDto(post);

        assertNotNull(redisPostDto);
        assertEquals(post.getId(), redisPostDto.id());
        assertEquals(post.getContent(), redisPostDto.content());
        assertEquals(post.getAuthorId(), redisPostDto.authorId());
        assertEquals(post.getProjectId(), redisPostDto.projectId());
        assertEquals(post.getLikes().size(), redisPostDto.likeCount());
        assertEquals(3, redisPostDto.recentComments().size());
        assertTrue(redisPostDto.hashtagIds().containsAll(post.getHashtags().stream().map(Hashtag::getId).collect(Collectors.toSet())));
    }

    @Test
    void toRecentCommentsShouldReturnEmptyListIfNoComments() {
        List<CacheCommentDto> recentComments = postMapper.toRecentComments(null);

        assertNotNull(recentComments);
        assertTrue(recentComments.isEmpty());
    }

    @Test
    void toRecentCommentsShouldMapAndSortRecentComments() {
        List<CacheCommentDto> recentComments = postMapper.toRecentComments(comments);

        assertNotNull(recentComments);
        assertEquals(3, recentComments.size());
        assertTrue(recentComments.get(0).createdAt().isAfter(recentComments.get(1).createdAt()));
    }

    @Test
    void toHashtagIdsShouldReturnEmptySetIfNoHashtags() {
        Set<Long> hashtagIds = postMapper.toHashtagIds(null);

        assertNotNull(hashtagIds);
        assertTrue(hashtagIds.isEmpty());
    }

    @Test
    void toHashtagIds_ShouldMapHashtagIds() {
        Set<Hashtag> hashtags = createTestHashtags();

        Set<Long> hashtagIds = postMapper.toHashtagIds(hashtags);

        assertNotNull(hashtagIds);
        assertEquals(hashtags.size(), hashtagIds.size());
        assertTrue(hashtagIds.containsAll(hashtags.stream().map(Hashtag::getId).collect(Collectors.toSet())));
    }


    private Post createTestPost() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("Test content");
        post.setAuthorId(101L);
        post.setProjectId(202L);
        post.setLikes(List.of(new Like(), new Like()));
        post.setHashtags(createTestHashtags());
        post.setCreatedAt(LocalDateTime.now().minusDays(2));
        post.setPublishedAt(LocalDateTime.now());
        post.setComments(createTestComments(post));
        return post;
    }

    private List<Comment> createTestComments(Post post) {
        Comment comment1 = new Comment(1L,  "Comment 1", 201L,  List.of(new Like()),post, LocalDateTime.now().minusMinutes(10),LocalDateTime.now().minusMinutes(5));
        Comment comment2 = new Comment(2L, "Comment 2", 202L, List.of(new Like(), new Like()),post, LocalDateTime.now().minusMinutes(5),LocalDateTime.now().minusMinutes(3));
        Comment comment3 = new Comment(3L,  "Comment 3",203L, List.of(), post, LocalDateTime.now().minusMinutes(3), LocalDateTime.now().minusMinutes(2));
        Comment comment4 = new Comment(4L,  "Comment 4", 204L, List.of(),post, LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(15));
        return List.of(comment1, comment2, comment3, comment4);
    }

    private Set<Hashtag> createTestHashtags() {
        Hashtag hashtag1 = new Hashtag(1L, "#Tag1",LocalDateTime.now());
        Hashtag hashtag2 = new Hashtag(2L, "#Tag2",LocalDateTime.now());
        return Set.of(hashtag1, hashtag2);
    }
}