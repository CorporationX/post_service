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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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


    @Test
    void testCreateCommentWithOptionalImagePostNotFoundThrows() {
        when(postRepository.findById(123L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            commentService.createCommentWithOptionalImage(
                    "text", 1L, 123L, null);
        });
        assertTrue(ex.getMessage().contains("Post not found: 123"));

        verify(postRepository, times(1)).findById(123L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentRepository, imageService, s3StorageService);
    }

    @Test
    void testCreateCommentWithOptionalImageWithoutFileSuccess() throws IOException {
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
        mappedDto.setAuthorId(5L);
        mappedDto.setPostId(10L);

        when(postRepository.findById(any())).thenReturn(Optional.of(fakePost));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(mappedDto);

        CommentDto result = commentService.createCommentWithOptionalImage(
                "hello",
                5L,
                10L,
                null);

        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals("hello", result.getContent());

        verify(postRepository, times(1)).findById(10L);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper).toDto(savedComment);

        verifyNoInteractions(imageService, s3StorageService);
    }

    @Test
    void testCreateCommentWithOptionalImageWithFileSuccess() throws IOException {
        Post fakePost = new Post();
        fakePost.setId(11L);

        Comment initialComment = Comment.builder()
                .content("with image")
                .authorId(7L)
                .post(fakePost)
                .build();

        Comment commentWithKeys = Comment.builder()
                .id(100L)
                .content("with image")
                .authorId(7L)
                .post(fakePost)
                .largeImageFileKey("comments/images/large/someuuid_large.png")
                .smallImageFileKey("comments/images/small/someuuid_small.png")
                .build();

        CommentDto dtoStub = new CommentDto();
        dtoStub.setId(100L);
        dtoStub.setContent("with image");
        dtoStub.setLargeObjectKey(commentWithKeys.getLargeImageFileKey());
        dtoStub.setSmallObjectKey(commentWithKeys.getSmallImageFileKey());

        when(postRepository.findById(11L)).thenReturn(Optional.of(fakePost));


        when(commentRepository.save(any(Comment.class)))
                .thenReturn(initialComment)
                .thenReturn(commentWithKeys);

        when(commentMapper.toDto(commentWithKeys)).thenReturn(dtoStub);

        String contentType = "image/png";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "img.png",
                contentType,
                new byte[]{1, 2, 3, 4}
        );

        byte[] dummyLarge = new byte[]{10, 11, 12};
        byte[] dummySmall = new byte[]{20, 21, 22};
        when(imageService.createLargeImage(file)).thenReturn(dummyLarge);
        when(imageService.createSmallImage(file)).thenReturn(dummySmall);
        when(imageService.getFileExtension("img.png")).thenReturn("png");
        when(imageService.getResizedImageContentType(contentType)).thenReturn(contentType);

        when(s3StorageService.generatePresignedUrl(anyString()))
                .thenReturn("https://s3/large-url", "https://s3/small-url");

        CommentDto result = commentService.createCommentWithOptionalImage(
                "with image", 7L, 11L, file);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("with image", result.getContent());
        assertEquals("https://s3/large-url", result.getUrlLarge());
        assertEquals("https://s3/small-url", result.getUrlThumb());

        verify(postRepository, times(1)).findById(11L);
        verify(commentRepository, times(2)).save(any(Comment.class));

        verify(imageService).createLargeImage(file);
        verify(imageService).createSmallImage(file);

        verify(s3StorageService).uploadFile(
                startsWith("comments/images/large/"),
                any(InputStream.class),
                eq((long) dummyLarge.length),
                eq(contentType));
        verify(s3StorageService).uploadFile(
                startsWith("comments/images/small/"),
                any(InputStream.class),
                eq((long) dummySmall.length),
                eq(contentType));

        verify(commentMapper).toDto(commentWithKeys);
        verify(s3StorageService).generatePresignedUrl(commentWithKeys.getLargeImageFileKey());
        verify(s3StorageService).generatePresignedUrl(commentWithKeys.getSmallImageFileKey());
    }

    @Test
    void testDeleteCommentWithImagesDeletesFromS3AndRepository() {
        Comment existing = Comment.builder()
                .id(50L)
                .content("to delete")
                .authorId(2L)
                .largeImageFileKey("comments/images/large/x.png")
                .smallImageFileKey("comments/images/small/x.png")
                .build();

        when(commentRepository.findById(50L)).thenReturn(Optional.of(existing));

        commentService.deleteComment(50L);

        verify(commentRepository, times(1)).findById(50L);
        verify(s3StorageService, times(1))
                .deleteFile("comments/images/large/x.png");
        verify(s3StorageService, times(1))
                .deleteFile("comments/images/small/x.png");
        verify(commentRepository, times(1)).delete(existing);
    }

    @Test
    void testDeleteCommentNotFoundThrows() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteComment(999L);
        });
        assertTrue(ex.getMessage().contains("Comment not found: 999"));

        verify(commentRepository, times(1)).findById(999L);
        verifyNoMoreInteractions(s3StorageService);
    }
}
