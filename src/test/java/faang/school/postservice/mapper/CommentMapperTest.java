package faang.school.postservice.mapper;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.kafka.event.comment.CommentAddedEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private static final long RANDOM_ID = 1L;
    private static final String RANDOM_CONTENT = "Random content";
    private static final long RANDOM_OTHER_ID = 2L;

    private CommentMapperImpl mapper;
    private CommentDto dto;
    private Comment comment;
    private CommentAddedEvent commentAddedEvent;
    private CommentRedis commentRedis;

    @BeforeEach
    public void setUp() {
        //Arrange
        mapper = new CommentMapperImpl();

        Like firstLike = new Like();
        firstLike.setId(RANDOM_ID);
        Like secondLike = new Like();
        secondLike.setId(RANDOM_OTHER_ID);
        Post post = new Post();
        post.setId(RANDOM_ID);

        comment = new Comment();
        comment.setId(RANDOM_ID);
        comment.setContent(RANDOM_CONTENT);
        comment.setAuthorId(RANDOM_ID);
        comment.setLikes(List.of(firstLike, secondLike));
        comment.setPost(post);

        dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorId(comment.getAuthorId());
        dto.setLikesId(List.of(firstLike.getId(), secondLike.getId()));
        dto.setPostId(comment.getPost().getId());

        commentAddedEvent = CommentAddedEvent.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .postId(comment.getPost().getId())
                .build();

        commentRedis = CommentRedis.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(UserRedis.builder()
                        .id(comment.getAuthorId())
                        .build())
                .postId(comment.getPost().getId())
                .build();
    }

    @Test
    void testToDto() {
        //Assert
        CommentDto actual = mapper.toDto(comment);
        assertEquals(dto, actual);
    }

    @Test
    void testToEntity() {
        //Act
        Comment entity = mapper.toEntity(dto);
        //Assert
        assertEquals(comment.getId(), entity.getId());
        assertEquals(comment.getContent(), entity.getContent());
        assertEquals(comment.getAuthorId(), entity.getAuthorId());
    }

    @Test
    void testToCommentEvent() {
        CommentAddedEvent actual = mapper.toCommentEvent(comment);
        assertEquals(commentAddedEvent, actual);
    }

    @Test
    void testToRedisFromCommentAddedEvent() {
        CommentRedis actual = mapper.toRedis(commentAddedEvent);
        assertEquals(commentRedis, actual);
    }

    @Test
    void testToRedisFromComment() {
        CommentRedis actual = mapper.toRedis(comment);
        assertEquals(commentRedis, actual);
    }

    @Test
    void testToRedisTreeSetFromComments() {
        Set<CommentRedis> expected = new TreeSet<>(Set.of(commentRedis));
        List<Comment> comments = List.of(comment);

        Set<CommentRedis> actual = mapper.toRedisTreeSet(comments);

        assertEquals(expected, actual);
    }
    @Test
    void testToCommentRedisListFromComments() {
        List<CommentRedis> expected = new LinkedList<>(List.of(commentRedis));
        List<Comment> comments = List.of(comment);

        List<CommentRedis> actual = mapper.toRedis(comments);

        assertEquals(expected, actual);
    }

    @Test
    void testLikesIsNull() {
        //Assert
        assertEquals(List.of(), mapper.likesToLikesId(null));
    }

    @Test
    void testLikesToLikesIdValid() {
        //Arrange
        List<Like> likes = comment.getLikes();
        List<Long> likesId = dto.getLikesId();
        //Act
        List<Long> actualList = mapper.likesToLikesId(likes);
        //Assert
        assertEquals(likesId, actualList);
    }
}