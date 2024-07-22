package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private static final long RANDOM_ID = 1L;
    private static final String RANDOM_CONTENT = "Random content";
    private static final long RANDOM_OTHER_ID = 2L;

    private CommentMapperImpl mapper;
    private CommentDto dto;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        mapper = new CommentMapperImpl();

        Like likeOne = new Like();
        Like likeTwo = new Like();
        Post post = new Post();
        likeOne.setId(RANDOM_ID);
        likeTwo.setId(RANDOM_OTHER_ID);
        post.setId(RANDOM_ID);

        dto = new CommentDto();
        dto.setId(RANDOM_ID);
        dto.setContent(RANDOM_CONTENT);
        dto.setAuthorId(RANDOM_ID);
        dto.setLikesId(List.of(RANDOM_ID, RANDOM_OTHER_ID));
        dto.setPostId(RANDOM_ID);

        comment = new Comment();
        comment.setId(RANDOM_ID);
        comment.setContent(RANDOM_CONTENT);
        comment.setAuthorId(RANDOM_ID);
        comment.setLikes(List.of(likeOne, likeTwo));
        comment.setPost(post);
    }

    @Test
    void testToDto() {
        assertEquals(dto, mapper.toDto(comment));
    }

    @Test
    void testToEntity() {
        Comment entity = mapper.toEntity(dto);
        assertEquals(comment.getId(), entity.getId());
        assertEquals(comment.getContent(), entity.getContent());
        assertEquals(comment.getAuthorId(), entity.getAuthorId());
    }

    @Test
    void testLikesIsNull() {
        assertEquals(List.of(), mapper.likesToLikesId(null));
    }

    @Test
    void testLikesToLikesIdValid() {
        List<Like> likes = comment.getLikes();
        List<Long> likesId = dto.getLikesId();
        assertEquals(likesId, mapper.likesToLikesId(likes));
    }
}