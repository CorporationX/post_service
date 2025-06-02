package faang.school.postservice.util.service;

import faang.school.postservice.dto.image.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.image.ImageProcessingService;
import faang.school.postservice.service.s3.S3StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

    @Mock
    private S3StorageService s3StorageService;

    @Mock
    private ImageProcessingService imageService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCommentWithOptionalImagePostNotFoundThrows() {
        when(postRepository.findById(123L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            commentService.createCommentWithOptionalImage("text", 1L, 123L, null);
        });
        assertTrue(ex.getMessage().contains("Post not found: 123"));
        verify(postRepository, times(1)).findById(123L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentRepository, imageService, s3StorageService);
    }

    @Test
    void createCommentWithOptionalImage_WithoutFile_Success() throws IOException {
        // Сценарий: создаём комментарий без изображения
        Post fakePost = new Post();
        fakePost.setId(10L);

        Comment savedComment = Comment.builder()
                .id(42L)
                .content("hello")
                .authorId(5L)
                .post(fakePost)
                .build();

        CommentDto mappedDto = new CommentDto();
        mappedDto.setId(42L);
        mappedDto.setContent("hello");

        when(postRepository.findById(10L)).thenReturn(Optional.of(fakePost));
        // Первый save создаёт Comment c ID null, второй save возвращает savedComment
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(mappedDto);

        CommentDto result = commentService.createCommentWithOptionalImage("hello", 5L, 10L, null);

        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals("hello", result.getContent());

        verify(postRepository, times(1)).findById(10L);
        verify(commentRepository, times(2)).save(any(Comment.class));
        verify(commentMapper).toDto(savedComment);
        verifyNoInteractions(imageService, s3StorageService);
    }
}
