package faang.school.postservice.util.service;

import faang.school.postservice.dto.image.CommentImageDto;
import faang.school.postservice.mapper.comment.CommentImageMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentImageService;
import faang.school.postservice.service.image.ImageProcessingService;
import faang.school.postservice.service.s3.PresignService;
import faang.school.postservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

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
public class CommentImageServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageProcessingService imageService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentImageMapper commentMapper;

    @Mock
    private PresignService presignService;

    @InjectMocks
    private CommentImageService commentService;

    @Test
    void testCreateCommentWithOptionalImagePostNotFoundThrows() {
        when(postRepository.findById(123L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            commentService.createCommentWithOptionalImage("text", 1L, 123L, null);
        });
        assertTrue(ex.getMessage().contains("Post not found: 123"));

        verify(postRepository, times(1)).findById(123L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentRepository, imageService, s3Service, presignService, commentMapper);
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

        CommentImageDto mappedDto = new CommentImageDto();
        mappedDto.setId(42L);
        mappedDto.setContent("hello");
        mappedDto.setAuthorId(5L);
        mappedDto.setPostId(10L);

        when(postRepository.findById(10L)).thenReturn(Optional.of(fakePost));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(mappedDto);

        CommentImageDto result = commentService.createCommentWithOptionalImage(
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

        verifyNoInteractions(imageService, s3Service, presignService);
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

        String largeKey = "comments/" + fakePost.getId() + "/comments/ID_PLACEHOLDER/large_uuid.png";
        String smallKey = "comments/" + fakePost.getId() + "/comments/ID_PLACEHOLDER/small_uuid.png";

        Comment commentWithKeys = Comment.builder()
                .id(100L)
                .content("with image")
                .authorId(7L)
                .post(fakePost)
                .largeImageFileKey(largeKey)
                .smallImageFileKey(smallKey)
                .build();

        CommentImageDto dtoStub = new CommentImageDto();
        dtoStub.setId(100L);
        dtoStub.setContent("with image");
        dtoStub.setUrlLarge("https://signed/large-url");
        dtoStub.setUrlThumb("https://signed/small-url");
        dtoStub.setAuthorId(7L);
        dtoStub.setPostId(11L);

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

        when(imageService.getFileExtension("img.png")).thenReturn("png");
        when(imageService.getResizedImageContentType(contentType)).thenReturn(contentType);

        byte[] dummyLarge = new byte[]{10, 11, 12};
        byte[] dummySmall = new byte[]{20, 21, 22};
        when(imageService.createLargeImage(file)).thenReturn(dummyLarge);
        when(imageService.createSmallImage(file)).thenReturn(dummySmall);

        when(s3Service.uploadBytesAsResource(any(), anyString(), anyString()))
                .thenReturn(null);

        when(presignService.generatePresignedUrl(largeKey))
                .thenReturn("https://signed/large-url");
        when(presignService.generatePresignedUrl(smallKey))
                .thenReturn("https://signed/small-url");

        CommentImageDto result = commentService.createCommentWithOptionalImage(
                "with image", 7L, 11L, file);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("with image", result.getContent());
        assertEquals("https://signed/large-url", result.getUrlLarge());
        assertEquals("https://signed/small-url", result.getUrlThumb());

        verify(postRepository, times(1)).findById(11L);
        verify(commentRepository, times(2)).save(any(Comment.class));

        verify(imageService).createLargeImage(file);
        verify(imageService).createSmallImage(file);

        verify(s3Service).uploadBytesAsResource(eq(dummyLarge), startsWith("comments/11/comments/"), eq(contentType));
        verify(s3Service).uploadBytesAsResource(eq(dummySmall), startsWith("comments/11/comments/"), eq(contentType));

        verify(presignService).generatePresignedUrl(largeKey);
        verify(presignService).generatePresignedUrl(smallKey);

        verify(commentMapper).toDto(commentWithKeys);
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

        commentService.deleteCommentWithImage(50L);

        verify(commentRepository, times(1)).findById(50L);
        verify(s3Service, times(1)).deleteFile("comments/images/large/x.png");
        verify(s3Service, times(1)).deleteFile("comments/images/small/x.png");
        verify(commentRepository, times(1)).delete(existing);

        verifyNoMoreInteractions(s3Service, commentRepository);
    }

    @Test
    void testDeleteCommentNotFoundThrows() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteCommentWithImage(999L);
        });
        assertTrue(ex.getMessage().contains("Comment not found: 999"));

        verify(commentRepository, times(1)).findById(999L);
        verifyNoMoreInteractions(s3Service, presignService, commentRepository);
    }
}
