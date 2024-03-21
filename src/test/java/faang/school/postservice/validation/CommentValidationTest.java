//package faang.school.postservice.validation;
//
//import faang.school.postservice.client.UserServiceClient;
//import faang.school.postservice.dto.CommentDto;
//import faang.school.postservice.model.Comment;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class CommentValidationTest {
//    @Mock
//    UserServiceClient userServiceClient;
//    @InjectMocks
//    CommentValidation commentValidation;
//
//    CommentDto firstCommentDto;
//    CommentDto secondCommentDto;
//
//    Comment firstComment;
//    Comment secondComment;
//    @BeforeEach
//    void setUp() {
//        firstCommentDto = CommentDto.builder()
//                .id(null)
//                .content("first content")
//                .authorId(1L)
//                .likesIds(null)
//                .build();
//        firstComment = Comment.builder()
//                .id(1L)
//                .content("first content")
//                .authorId(1L)
//                .likes(null)
//                .build();
//        secondCommentDto = CommentDto.builder()
//                .id(2L)
//                .content("second content")
//                .authorId(1L)
//                .likesIds(null)
//                .build();
//        secondComment = Comment.builder()
//                .id(2L)
//                .content("edited second content")
//                .authorId(1L)
//                .likes(null)
//                .build();
//
//    }
//
//
//    @Test
//    public void testAuthorValidation(){
//        commentValidation.authorValidation(firstCommentDto.getAuthorId());
//
//    }
//}