package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentFiltersDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.mapper.comment.LikeMapperImpl;
import faang.school.postservice.mapper.comment.PostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.postservice.service.comment.TestData.createLike;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static faang.school.postservice.service.comment.TestData.createComment;
import static faang.school.postservice.service.comment.TestData.createCommentRequestDto;
import static faang.school.postservice.service.comment.TestData.createPost;
import static faang.school.postservice.service.comment.TestData.createUserDto;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    public static long MILLISECOND_IN_SEC = 1000;
    public static long SLEEP_TIME_SEC = 1;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    private CommentMapperImpl commentMapper;

    @Spy
    private LikeMapperImpl likeMapper;

    @Spy
    private PostMapperImpl postMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    private long authorId;
    private long commentId;
    private long postId;
    private Post post;
    private Comment comment;
    private UserDto userDto;
    private Like like1;
    private Like like2;
    private Like like3;
    private List<Like> likes;
    private List<Long> likeIds;
    private CommentRequestDto commentRequestDto;
    private CommentUpdateDto commentUpdateDto;
    private CommentFiltersDto commentFiltersDto;

    @BeforeEach
    void setUp() {
        authorId = 1L;
        postId = 1L;
        commentId = 1L;

        commentMapper.setLikeMapper(likeMapper);
        commentMapper.setPostMapper(postMapper);

        post = createPost(postId, authorId);
        userDto = createUserDto(authorId, "Author", "email");
        commentRequestDto = createCommentRequestDto("Текстовый комментарий", authorId, postId);
        comment = createComment(commentId, commentRequestDto.content(), authorId, post);
        commentUpdateDto = CommentUpdateDto.builder()
                .content("Новый текст комментария")
                .build();
        commentFiltersDto = CommentFiltersDto.builder()
                .postId(postId)
                .build();
        like1 = createLike(1L, authorId, post, comment);
        like2 = createLike(2L, 2L, post, comment);
        like3 = createLike(3L, 3L, post, comment);
        likes = List.of(like1, like2, like3);
        likeIds = Arrays.asList(like1.getId(), like2.getId(), like3.getId());
    }

    @Test
    void testCreateCommentSuccess() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment savedComment = invocation.getArgument(0);
                    savedComment.setId(commentId);
                    savedComment.setLikes(likes);
                    return savedComment;
                });

        CommentResponseDto commentResponseDtoFromDb;
        commentResponseDtoFromDb = commentService.createComment(commentRequestDto);

        verifyNoMoreInteractions(userServiceClient, commentRepository, postRepository);
        verify(commentRepository, Mockito.times(1))
                .save(commentArgumentCaptor.capture());
        assertEquals(commentRequestDto.content(), commentArgumentCaptor.getValue().getContent());

        assertNotNull(commentResponseDtoFromDb);
        assertEquals(commentId, commentResponseDtoFromDb.id());
        assertEquals(commentRequestDto.authorId(), commentResponseDtoFromDb.authorId());
        assertEquals(commentRequestDto.postId(), commentResponseDtoFromDb.postDto().id());
        assertEquals(commentRequestDto.content(), commentResponseDtoFromDb.content());
        assertEquals(likeIds.get(0), commentResponseDtoFromDb.likeDtos().get(0).id());
        assertEquals(likeIds.get(1), commentResponseDtoFromDb.likeDtos().get(1).id());
        assertEquals(likeIds.get(2), commentResponseDtoFromDb.likeDtos().get(2).id());
    }

    @Test
    void testCreateCommentIfUserNotFoundFailed() {
        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(commentRequestDto));
        assertEquals(String.format("User with id %s not found", authorId), exception.getMessage());
    }

    @Test
    void testCreateCommentIfPostNotFoundFailed() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(commentRequestDto));
        assertEquals(String.format("Post with id %s not found.", postId), exception.getMessage());
    }

    @Test
    void testUpdateCommentSuccess() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment savedComment = invocation.getArgument(0);
                    savedComment.setId(commentId);
                    savedComment.setLikes(likes);
                    return savedComment;
                });
        CommentResponseDto commentResponseDtoFromDb;
        commentResponseDtoFromDb = commentService.updateComment(commentId, authorId, commentUpdateDto);

        verifyNoMoreInteractions(userServiceClient, commentRepository, postRepository);
        verify(commentRepository, Mockito.times(1))
                .save(commentArgumentCaptor.capture());
        assertEquals(commentUpdateDto.content(), commentArgumentCaptor.getValue().getContent());

        assertNotNull(commentResponseDtoFromDb);
        assertEquals(commentId, commentResponseDtoFromDb.id());
        assertEquals(authorId, commentResponseDtoFromDb.authorId());
        assertEquals(postId, commentResponseDtoFromDb.postDto().id());
        assertEquals(commentUpdateDto.content(), commentResponseDtoFromDb.content());
        assertEquals(likeIds.get(0), commentResponseDtoFromDb.likeDtos().get(0).id());
        assertEquals(likeIds.get(1), commentResponseDtoFromDb.likeDtos().get(1).id());
        assertEquals(likeIds.get(2), commentResponseDtoFromDb.likeDtos().get(2).id());
    }

    @Test
    void testUpdateCommentIfUserNotAuthorFailed() {
        Long wrongAuthorId = 5L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        CommentValidationException exception = assertThrows(CommentValidationException.class,
                () -> commentService.updateComment(commentId, wrongAuthorId, commentUpdateDto));
        assertEquals(String.format("User with id %s is not allowed to update this comment.", wrongAuthorId),
                exception.getMessage());
    }

    @Test
    void testUpdateCommentIfCommentNotFoundFailed() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(commentId, authorId, commentUpdateDto));
        assertEquals(String.format("Comment with id %d not found", commentId), exception.getMessage());
    }

    @Test
    void testDeleteCommentSuccess() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.deleteComment(commentId);
        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(commentId);
        verifyNoMoreInteractions(userServiceClient, commentRepository, postRepository);
    }

    @Test
    void testDeleteCommentIfCommentNotFoundFailed() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.deleteComment(commentId));
        assertEquals(String.format("Comment with id %d not found", commentId), exception.getMessage());
    }

    @Test
    void testGetCommentsByIdSuccess() {
        Comment comment1 = createComment(commentId, commentRequestDto.content(), authorId, post);
        sleepSec(SLEEP_TIME_SEC);
        Comment comment2 = createComment(commentId, commentRequestDto.content(), authorId, post);
        sleepSec(SLEEP_TIME_SEC);
        Comment comment3 = createComment(commentId, commentRequestDto.content(), authorId, post);
        List<Comment> comments = List.of(comment, comment1, comment2, comment3);
        when(commentRepository.findAllByPostId(post.getId())).thenReturn(comments);

        List<CommentResponseDto> responseDtos = commentService.getComments(commentFiltersDto);

        assertNotNull(responseDtos);
        assertEquals(responseDtos.size(), comments.size());
        assertEquals(responseDtos.get(0).createdAt(), comment3.getCreatedAt());
        assertEquals(responseDtos.get(1).createdAt(), comment2.getCreatedAt());
        assertEquals(responseDtos.get(2).createdAt(), comment1.getCreatedAt());
        assertEquals(responseDtos.get(3).createdAt(), comment.getCreatedAt());
    }

    private void sleepSec(long sec) {
        try {
            Thread.sleep(sec * MILLISECOND_IN_SEC);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
