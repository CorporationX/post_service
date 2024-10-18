package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentServiceImpl;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CommentServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapperImpl commentMapper;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @InjectMocks
    private CommentServiceImpl commentService;

    private long authorId;
    private long commentId;
    private long postId;
    private Post post;
    private Comment mockComment;

    private CommentDto commentDto;
    private UpdateCommentDto updateCommentDto;

    @BeforeEach
    void setUp() {
        authorId = 1L;
        postId = 1L;
        commentId = 1L;

        post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .content("Тестовый комментарий")
                .authorId(authorId)
                .postId(postId)
                .build();

         mockComment = Comment.builder()
                .id(commentId)
                .content("Старое содержимое")
                .authorId(authorId)
                .post(post)
                .build();

        updateCommentDto = UpdateCommentDto.builder()
                .content("Новый текстовый комментарий")
                .authorId(authorId)
                .build();
    }

    @Test
    void deleteComment() {
        long commentId = 3L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        commentService.deleteComment(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void addComment_WhenOk() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentMapper.toComment(commentDto)).thenReturn(mockComment);

        commentService.addComment(commentDto);

        verify(postRepository).findById(postId);
        verify(userServiceClient).getUser(authorId);
        verify(commentRepository).save(any());
        verify(commentEventPublisher).publish(any());
        verify(commentMapper).toDto((Comment) any());
    }

    @Test
    void addComment_WhenUserNotExists() {
        when(userServiceClient.getUser(authorId)).thenThrow(FeignException.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(Exception.class, () -> commentService.addComment(commentDto));

        verify(userServiceClient).getUser(authorId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    void addComment_WhenNotExistsPosts() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.addComment(commentDto));

        verify(postRepository).findById(postId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    void updateComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        commentService.updateComment(commentId, updateCommentDto);

        verify(commentRepository, times(1))
                .updateContentAndDateById(eq(commentId), eq(updateCommentDto.content()), any(LocalDateTime.class));
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = List.of(
                Comment.builder().id(1L).content("Комментарий 1").post(post).build(),
                Comment.builder().id(2L).content("Комментарий 2").post(post).build()
        );
        List<CommentDto> commentDto = List.of(
                CommentDto.builder().id(1L).content("Комментарий 1").postId(postId).build(),
                CommentDto.builder().id(2L).content("Комментарий 2").postId(postId).build()
        );

        when(commentRepository.getByPostIdOrderByCreatedAtDesc(postId)).thenReturn(comments);
        when(commentMapper.toDto(comments)).thenReturn(commentDto);

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertEquals(commentDto, result);
        verify(commentRepository).getByPostIdOrderByCreatedAtDesc(postId);
        verify(commentMapper).toDto(comments);
    }
}
