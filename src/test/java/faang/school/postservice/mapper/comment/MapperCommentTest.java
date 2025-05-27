//package faang.school.postservice.mapper.comment;
//
//import faang.school.postservice.dto.comment.CommentDtoResponse;
//import faang.school.postservice.dto.comment.CreateCommentDto;
//import faang.school.postservice.dto.comment.UpdateCommentDto;
//import faang.school.postservice.model.Comment;
//import faang.school.postservice.model.Post;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mapstruct.Mapper;
//import org.mapstruct.factory.Mappers;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class MapperCommentTest {
//    public static final long COMMENT_ID = 11L;
//    public static final String COMMENT_CONTENT = "CONTENT";
//    public static final long COMMENT_AUTHOR_ID = 22L;
//    public static final long POST_ID = 33L;
//    public static final LocalDateTime TIME_NOW = LocalDateTime.now();
//
//    //private final MapperComment mapperComment = Mappers.getMapper(MapperComment.class);
//    private MapperComment mapperComment;
//    private CommentDtoResponse commentDtoResponse;
//    private CreateCommentDto createCommentDto;
//    private UpdateCommentDto updateCommentDto;
//    private Comment comment;
//
//    @BeforeEach
//    void setUp() {
//        Post post = new Post();
//        post.setId(POST_ID);
//        comment = new Comment();
//        comment.setId(COMMENT_ID);
//        comment.setContent(COMMENT_CONTENT);
//        comment.setAuthorId(COMMENT_AUTHOR_ID);
//        comment.setPost(post);
//        comment.setCreatedAt(TIME_NOW);
//        comment.setUpdatedAt(TIME_NOW);
//    }
//
//    @Test
//    void fromEntityToCreateDto() {
//
//        CommentDtoResponse result = mapperComment.fromEntityToCreateDto(comment);
//
//        assertEquals(COMMENT_ID, result.getCommentId());
//        assertEquals(COMMENT_CONTENT, result.getContent());
//        assertEquals(COMMENT_AUTHOR_ID, result.getAuthorId());
//        assertEquals(POST_ID, result.getPostId());
//        //assertEquals(TIME_NOW.toString(), result.getCreateData());
//        //assertNotEquals(TIME_NOW, commentDtoResponse.get)
//
//
//    }
//
//    @Test
//    void fromEntityToModifiedDto() {
//    }
//
//    @Test
//    void fromCreatDtoToEntity() {
//    }
//
//    @Test
//    void fromUpdateDtoToEntity() {
//    }
//
//    @Test
//    void mapPostToId() {
//    }
//}