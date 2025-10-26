package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exeption.ResourceNotFoundException;
import faang.school.postservice.exeption.ValidationException;
import faang.school.postservice.helpers.TestUtils;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentValidator commentValidator;

    private CommentCreateDto validCreateDto;
    private CommentUpdateDto validUpdateDto;

    private Post samplePost;
    private Comment sampleComment;

    @BeforeEach
    void init() {
        validCreateDto = new CommentCreateDto("Test content", 1L,null, null);
        validUpdateDto = new CommentUpdateDto("Updated content", null, null);

        samplePost = new Post();
        samplePost.setId(1L);

        sampleComment = new Comment();
        sampleComment.setId(10L);
        sampleComment.setAuthorId(1L);
        sampleComment.setPost(samplePost);
        sampleComment.setContent("Original content");
    }

    @Test
    void create_success() {
        when(postRepository.findById(validCreateDto.postId())).thenReturn(Optional.of(samplePost));
        doNothing().when(commentValidator).validateCommentContent(validCreateDto.content());
        doNothing().when(commentValidator).validateUser(anyLong(), any());
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Comment created = commentService.create(validCreateDto, 1L);

        assertNotNull(created);
        verify(commentValidator).validateCommentContent(validCreateDto.content());
        verify(commentValidator).validateUser(1L, userServiceClient);
        verify(commentRepository).save(any());
    }

    @Test
    void create_postNotFound_throws() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        TestUtils.assertThrowsAny(ResourceNotFoundException.class,
                () -> commentService.create(validCreateDto, 1L));
    }

    @Test
    void update_success() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(sampleComment));
        doNothing().when(commentValidator).validateCommentContent(validUpdateDto.content());
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Comment updated = commentService.update(10L, validUpdateDto, 1L);

        assertEquals(validUpdateDto.content(), updated.getContent());
    }

    @Test
    void update_notAuthor_throws() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(sampleComment));

        TestUtils.assertThrowsAny(ValidationException.class,
                () -> commentService.update(10L, validUpdateDto, 2L));
    }

    @Test
    void delete_success() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(sampleComment));
        doNothing().when(commentRepository).delete(sampleComment);

        assertDoesNotThrow(() -> commentService.delete(10L, 1L));
        verify(commentRepository).delete(sampleComment);
    }

    @Test
    void delete_notAuthor_throws() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(sampleComment));

        TestUtils.assertThrowsAny(ValidationException.class,
                () -> commentService.delete(10L, 2L));
    }

    @Test
    void getByPostId_success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(samplePost));
        when(commentRepository.findAllByPostId(1L)).thenReturn(List.of(sampleComment));

        List<CommentDto> dto = commentService.getByPostId(1L);

        assertEquals(1, dto.size());
    }

    @Test
    void getByPostId_postNotFound_throws() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getByPostId(1L));
    }

    @Test
    void getById_success() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(sampleComment));

        Comment comment = commentService.getById(10L);
        assertEquals(10L, comment.getId());
    }

    @Test
    void getById_notFound_throws() {
        when(commentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getById(10L));
    }
}