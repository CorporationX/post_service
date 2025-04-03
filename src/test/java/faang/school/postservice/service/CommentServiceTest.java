package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    private static final long AUTHOR_ID = 1L;
    private static final long NOT_AUTHOR_ID = 2L;
    private static final long COMMENT_ID = 2L;
    private static final long ELSE_COMMENT_ID = 3L;
    private static final long POST_ID = 3L;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private CommentProducer commentProducer;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void testCreateSuccessfully() {
        CommentCreateDto createDto = CommentCreateDto.builder()
                .authorId(AUTHOR_ID).postId(POST_ID)
                .build();
        Post post = Post.builder()
                .published(true)
                .deleted(false)
                .build();

        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(Comment.builder().authorId(AUTHOR_ID).build());

        commentService.create(createDto);
        verify(commentRepository, Mockito.times(1)).save(any());
        verify(commentProducer).publish(any());
    }

    @Test
    public void testCreateNotPublishedPost() {
        CommentCreateDto createDto = CommentCreateDto.builder()
                .authorId(AUTHOR_ID).postId(POST_ID)
                .build();
        Post post = Post.builder()
                .published(false)
                .build();

        when(postService.getPostById(POST_ID)).thenReturn(post);

        assertThrows(BusinessException.class, () -> commentService.create(createDto));
    }

    @Test
    public void testCreateDeletedPost() {
        CommentCreateDto createDto = CommentCreateDto.builder()
                .authorId(AUTHOR_ID).postId(POST_ID)
                .build();
        Post post = Post.builder()
                .published(true)
                .deleted(true)
                .build();

        when(postService.getPostById(POST_ID)).thenReturn(post);

        assertThrows(BusinessException.class, () -> commentService.create(createDto));
    }

    @Test
    public void testUpdateSuccessfully() {
        CommentUpdateDto updateDto = CommentUpdateDto.builder()
                .id(COMMENT_ID).editorId(AUTHOR_ID)
                .build();

        Comment comment = Comment.builder().id(COMMENT_ID).authorId(AUTHOR_ID).build();
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));

        commentMapper.updateEntityFromDto(updateDto, comment);

        commentService.update(updateDto);
        verify(commentRepository, Mockito.times(1)).save(comment);

    }

    @Test
    public void testUpdateFailsIfEditorIsNotTheAuthor() {
        CommentUpdateDto updateDto = CommentUpdateDto.builder()
                .editorId(NOT_AUTHOR_ID).id(COMMENT_ID)
                .build();

        Comment comment = Comment.builder().id(COMMENT_ID).authorId(AUTHOR_ID).build();
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.ofNullable(comment));

        assertThrows(BusinessException.class, () -> commentService.update(updateDto));
    }

    @Test
    public void testGetCommentsByPostId() {
        Comment comment1 = Comment.builder().id(COMMENT_ID).build();
        Comment comment2 = Comment.builder().id(ELSE_COMMENT_ID).build();
        List<Comment> comments = List.of(comment1, comment2);

        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<CommentReadDto> commentDtos = commentService.getCommentsByPostId(POST_ID);

        assertEquals(2, commentDtos.size());
    }

    @Test
    public void testRemoveSuccessfully() {
        commentService.remove(COMMENT_ID);
        verify(commentRepository, Mockito.times(1)).deleteById(COMMENT_ID);
    }
}
