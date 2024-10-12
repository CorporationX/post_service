package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.CommentValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final long POST_ID = 1L;
    private static final String CONTENT = "test";
    private static final long AUTHOR_ID = 1L;
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 9, 21, 11, 34, 54);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 9, 24, 11, 30, 23);
    private static final long COMMENT_ID = 1L;

    private CreateCommentRequest createCommentRequest;
    private Comment commentForDto;
    private Post post;
    private Comment commentForDB;
    private CommentDto commentDto;
    private CommentDto commentDto1;
    private CommentDto commentDto2;
    private UserDto userDto;
    private Comment comment;
    private Comment comment1;
    private Comment comment2;
    private UpdateCommentRequest updateCommentRequest;
    private List<Comment> comments;


    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentValidator commentValidator;


    @BeforeEach
    void init() {

        post = new Post();
        userDto = mock(UserDto.class);

        createCommentRequest = CreateCommentRequest.builder()
                .content(CONTENT)
                .authorId(AUTHOR_ID)
                .build();

        updateCommentRequest = UpdateCommentRequest.builder()
                .content(CONTENT)
                .authorId(AUTHOR_ID)
                .build();

        commentForDto = initializingComment(CONTENT, AUTHOR_ID);

        commentForDB = initializingComment(COMMENT_ID, CONTENT, AUTHOR_ID, post, CREATED_AT, UPDATED_AT);

        comment = initializingComment(COMMENT_ID, CONTENT, AUTHOR_ID, post, CREATED_AT, UPDATED_AT);
        comment1 = initializingComment(2L, CONTENT, 2L, post,
                LocalDateTime.of(2024, 3, 3, 5, 7, 23),
                LocalDateTime.of(2025, 9, 24, 11, 30, 23));
        comment2 = initializingComment(3L, CONTENT, 3L, post,
                LocalDateTime.of(2024, 10, 24, 11, 43, 43),
                LocalDateTime.of(2024, 11, 13, 16, 12, 32));

        comments = new ArrayList<>();
        comments.add(comment);
        comments.add(comment1);
        comments.add(comment2);

        commentDto = initializingCommentDto(COMMENT_ID, CONTENT, AUTHOR_ID, CREATED_AT, UPDATED_AT);
        commentDto1 = initializingCommentDto(2L, CONTENT, 2L,
                LocalDateTime.of(2024, 3, 3, 5, 7, 23),
                LocalDateTime.of(2025, 9, 24, 11, 30, 23));
        commentDto2 = initializingCommentDto(3L, CONTENT, 3L,
                LocalDateTime.of(2024, 10, 24, 11, 43, 43),
                LocalDateTime.of(2024, 11, 13, 11, 12, 32));
    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("successful event creation")
        void testSuccessfulCompletionCreateComment() {
            when(userServiceClient.getUser(createCommentRequest.getAuthorId())).thenReturn(userDto);
            when(commentMapper.toComment(createCommentRequest)).thenReturn(commentForDto);
            when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
            when(commentRepository.save(commentForDto)).thenReturn(commentForDB);
            when(commentMapper.toCommentDto(commentForDB)).thenReturn(commentDto);

            CommentDto result = commentService.createComment(POST_ID, createCommentRequest);

            verify(userServiceClient).getUser(createCommentRequest.getAuthorId());
            verify(commentMapper).toComment(createCommentRequest);
            verify(postRepository).findById(POST_ID);
            verify(commentRepository).save(commentForDto);
            verify(commentMapper).toCommentDto(commentForDB);

            assertNotNull(commentForDto.getPost());
            assertEquals(result.getId(), commentForDB.getId());
            assertEquals(result.getContent(), commentForDB.getContent());
            assertEquals(result.getAuthorId(), commentForDB.getAuthorId());
            assertEquals(result.getContent(), createCommentRequest.getContent());
            assertEquals(result.getAuthorId(), createCommentRequest.getAuthorId());
        }

        @Test
        @DisplayName("successful comment update")
        void testSuccessfulCompletionUpdateComment() {
            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
            doNothing().when(commentValidator).checkingForCompliance(comment, updateCommentRequest);
            when(commentMapper.toComment(updateCommentRequest)).thenReturn(commentForDto);
            when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
            when(commentRepository.save(commentForDto)).thenReturn(commentForDB);
            when(commentMapper.toCommentDto(commentForDB)).thenReturn(commentDto);

            CommentDto result = commentService.updateComment(POST_ID, 1L, updateCommentRequest);

            verify(commentRepository).findById(COMMENT_ID);
            verify(commentValidator).checkingForCompliance(comment, updateCommentRequest);
            verify(commentMapper).toComment(updateCommentRequest);
            verify(postRepository).findById(POST_ID);
            assertNotNull(commentForDto.getPost());
            verify(commentRepository).save(commentForDto);
            verify(commentMapper).toCommentDto(commentForDB);

            assertEquals(result.getId(), commentForDB.getId());
            assertEquals(result.getContent(), commentForDB.getContent());
            assertEquals(result.getAuthorId(), commentForDB.getAuthorId());
            assertEquals(result.getContent(), createCommentRequest.getContent());
            assertEquals(result.getAuthorId(), createCommentRequest.getAuthorId());
        }

        @Test
        @DisplayName("successful receipt of the list of comments")
        void testSuccessfulCompletionGetAllComments() {
            when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);
            when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
            when(commentMapper.toCommentDto(comment1)).thenReturn(commentDto1);
            when(commentMapper.toCommentDto(comment2)).thenReturn(commentDto2);

            List<CommentDto> result = commentService.getAllComments(POST_ID);

            CommentDto commentDtoWithMinDateTime = result.stream()
                    .min((dto1, dto2) -> dto1.getCreatedAt().compareTo(dto2.getCreatedAt()))
                    .orElse(null);

            CommentDto commentDtoWithMaxDateTime = result.stream()
                    .max((dto1, dto2) -> dto1.getCreatedAt().compareTo(dto2.getCreatedAt()))
                    .orElse(null);

            assertEquals(result.get(0), commentDtoWithMaxDateTime);
            assertEquals(result.get(result.size() - 1), commentDtoWithMinDateTime);
        }

        @Test
        @DisplayName("successful deletion of a comment")
        void testSuccessfulCompletionDeleteComment() {
            doNothing().when(commentRepository).deleteById(COMMENT_ID);

            assertDoesNotThrow(() -> commentService.deleteComment(COMMENT_ID));
        }
    }

    @Test
    @DisplayName("successfully throwing an exception when List of comments is Empty")
    void testGetAllCommentsWhenListIsEmpty() {
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(Collections.emptyList());

        assertThrows(NoSuchElementException.class, () -> commentService.getAllComments(POST_ID));
    }


    private Comment initializingComment(long commentId, String content, long authorId, Post post,
                                        LocalDateTime createdAt, LocalDateTime updateAt) {
        return Comment.builder()
                .id(commentId)
                .content(content)
                .authorId(authorId)
                .post(post)
                .createdAt(createdAt)
                .updatedAt(updateAt)
                .build();
    }

    private Comment initializingComment(String content, long authorId) {
        return Comment.builder()
                .content(content)
                .authorId(authorId)
                .build();
    }

    private CommentDto initializingCommentDto(long commentDtoId, String content,
                                              long authorId, LocalDateTime createAt, LocalDateTime updateAt) {
        return CommentDto.builder()
                .id(commentDtoId)
                .content(content)
                .authorId(authorId)
                .createdAt(createAt)
                .updatedAt(updateAt)
                .build();
    }

}