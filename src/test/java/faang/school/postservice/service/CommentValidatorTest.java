package faang.school.postservice.service;

import faang.school.postservice.exception.FileFormatException;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {

    @Spy
    private CommentValidator commentValidator;

    private Comment comment;
    private MultipartFile image;

    @BeforeEach
    public void setUp() {
        comment = Comment.builder()
                .content("content")
                .build();

        image = new MockMultipartFile("file",
                "test-file.png",
                "image/png",
                "exampledata".getBytes());
    }

    @Test
    public void validateCommentUpdateTest() {
        assertDoesNotThrow(() ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdateTest_modifiedCommentId() {
        comment.setId(1L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdateTest_modifiedAuthorId() {
        comment.setAuthorId(2L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdateTest_modifiedLargeImageKey() {
        comment.setLargeImageFileKey("222");
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateCommentUpdateTest_modifiedSmallImageKey() {
        comment.setSmallImageFileKey("444");
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateCommentUpdate(comment));
    }

    @Test
    public void validateAuthorTest() {
        comment.setAuthorId(1L);
        assertDoesNotThrow(() ->
                commentValidator.validateAuthor(comment, 1L));

    }

    @Test
    public void validateAuthor_differentUserIds() {
        comment.setAuthorId(2L);
        assertThrows(IllegalArgumentException.class, () ->
                commentValidator.validateAuthor(comment, 1L));
    }

    @Test
    public void validateImageSize() {
        ReflectionTestUtils.setField(commentValidator, "maxFileSize", "2MB");


        assertDoesNotThrow(() ->
                commentValidator.validateImageSize(image));
    }

    @Test
    public void validateImageSize_InvalidFileSizeBound() {
        ReflectionTestUtils.setField(commentValidator, "maxFileSize", "5GB");

        assertThrows(IllegalArgumentException.class, () ->
                        commentValidator.validateImageSize(image));
    }

    @Test
    public void validateImageSize_fileIsTooLarge() {
        ReflectionTestUtils.setField(commentValidator, "maxFileSize", "1MB");

        byte[] largeFileContent = new byte[2 * 1024 * 1024];
        MultipartFile largeImage = new MockMultipartFile(
                "file",
                "large-file.png",
                "image/png",
                largeFileContent);

        assertThrows(FileFormatException.class, () ->
                        commentValidator.validateImageSize(largeImage));
    }

    @Test
    public void validateImageFormat() {
        assertDoesNotThrow(() ->
                commentValidator.validateImageFormat(image));
    }

    @Test
    public void validateImageFormat_throwsFileFormatException() {
        MultipartFile image = new MockMultipartFile("file",
                "test-file.pdf",
                "file/pdf",
                "exampledata".getBytes());
        assertThrows(FileFormatException.class, () -> commentValidator.validateImageFormat(image));
    }

}
