package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.s3.AwsService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AwsService awsService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private ImageProcessor imageProcessor;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @Captor
    private ArgumentCaptor<Resource> resourceCaptor;

    private Comment comment;
    private Post post;
    private MultipartFile image;
    private InputStream inputStream;
    private byte[] testBytes = {1, 2, 3, 4};
    private final Long POST_ID = 1L;
    private final Long AUTHOR_ID = 1L;
    private final Long COMMENT_ID = 1L;
    private final int LARGE_IMAGE_MAX_SIZE = 1080;
    private final int SMALL_IMAGE_MAX_SIZE = 170;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(commentService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(commentService, "SMALL_IMAGE_MAX_SIZE", 170);
        ReflectionTestUtils.setField(commentService, "LARGE_IMAGE_MAX_SIZE", 1080);


        post = Post.builder()
                .id(POST_ID)
                .build();
        comment = Comment.builder()
                .id(COMMENT_ID)
                .content("exampleContent")
                .smallImageFileKey("smallKey")
                .largeImageFileKey("largeKey")
                .post(post)
                .build();
        image = new MockMultipartFile("file",
                "test-file.png",
                "image/png",
                "exampledata".getBytes());

        inputStream = new ByteArrayInputStream(testBytes);
    }

    @Test
    public void getCommentsByPostIdTest() {
        Comment comment1 = Comment.builder()
                .createdAt(LocalDateTime.of(2002, 9,4, 0, 0))
                .build();
        Comment comment2 = Comment.builder()
                .createdAt(LocalDateTime.of(2002, 1, 1, 0, 0))
                .build();
        List<Comment> comments = List.of(comment1, comment2);
        List<Comment> expected = List.of(comment2, comment1);

        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<Comment> result = commentService.getCommentsByPostId(POST_ID);

        assertEquals(expected, result);
        verify(commentRepository, times(1)).findAllByPostId(POST_ID);
        verify(postService, times(1)).get(POST_ID);
    }

    @Test
    public void createCommentTest() {
        when(postService.get(POST_ID)).thenReturn(post);

        commentService.createComment(comment, POST_ID, AUTHOR_ID);

        verify(userServiceClient, times(1)).getUser(AUTHOR_ID);
        verify(postService, times(1)).get(POST_ID);
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment captured = commentCaptor.getValue();
        assertEquals(post, captured.getPost());
        assertEquals(AUTHOR_ID, captured.getAuthorId());
    }

    @Test
    public void createCommentTest_throwsUserNotFoundException() {
        when(userServiceClient.getUser(AUTHOR_ID)).thenThrow(FeignException.NotFound.class);
        when(postService.get(POST_ID)).thenReturn(post);

        assertThrows(UserNotFoundException.class, () ->
                commentService.createComment(comment, POST_ID, AUTHOR_ID));
        verify(postService, times(1)).get(POST_ID);
    }

    @Test
    public void updateCommentTest() {
        Comment updatedComment = Comment.builder()
                        .content("xx")
                        .build();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.updateComment(COMMENT_ID, updatedComment, 1L);

        verify(commentValidator, times(1)).validateAuthor(comment, AUTHOR_ID);
        verify(commentValidator, times(1)).validateCommentUpdate(updatedComment);
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment actual = commentCaptor.getValue();
        assertEquals(updatedComment.getContent(), actual.getContent());
    }

    @Test
    public void updateCommentTest_throwsCommentNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.updateComment(COMMENT_ID, comment, AUTHOR_ID));
    }

    @Test
    public void deleteCommentTest() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.deleteComment(COMMENT_ID, AUTHOR_ID);

        verify(commentValidator, times(1)).validateAuthor(comment, AUTHOR_ID);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    public void deleteCommentTest_throwsCommentNotFoundException() {
        Long commentId = 1L;
        Long userId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId, userId));
    }

    @Test
    public void attachImageToCommentTest() throws IOException {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(imageProcessor.resizeImage(any(MultipartFile.class), anyInt())).thenReturn(mock(BufferedImage.class));
        when(imageProcessor.bufferedImageToByteArray(any(BufferedImage.class), anyString())).thenReturn(testBytes);

        commentService.attachImageToComment(COMMENT_ID, image, AUTHOR_ID);

        verify(commentValidator, times(1)).validateAuthor(comment, AUTHOR_ID);
        verify(commentValidator, times(1)).validateImageFormat(image);

        verify(resourceService, times(2)).deleteResourceByKey(anyString());
        verify(awsService, times(2)).deleteFile(anyString(), anyString());

        verify(imageProcessor, times(1)).resizeImage(image, SMALL_IMAGE_MAX_SIZE);
        verify(imageProcessor, times(1)).resizeImage(image, LARGE_IMAGE_MAX_SIZE);
        verify(imageProcessor, times(2)).bufferedImageToByteArray(any(BufferedImage.class), anyString());
        verify(awsService, times(2)).uploadFile(anyString(), anyString(), any(byte[].class));

        verify(resourceService, times(2)).createResource(resourceCaptor.capture());
        List<Resource> resources = resourceCaptor.getAllValues();
        Resource large = resources.get(0);
        Resource small = resources.get(1);
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment comment = commentCaptor.getValue();
        assertEquals(small.getKey(), comment.getSmallImageFileKey());
        assertEquals(large.getKey(), comment.getLargeImageFileKey());
    }

    @Test
    public void attachImageToComment_throwsCommentNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.attachImageToComment(COMMENT_ID, image, AUTHOR_ID));
    }

    @Test
    public void getCommentImageTest() throws IOException {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(awsService.downloadFile(anyString(), anyString())).thenReturn(testBytes);

        Function<Comment, String> keyExtractor = Comment::getLargeImageFileKey;
        byte[] result = commentService.getCommentImage(COMMENT_ID, keyExtractor);

        assertArrayEquals(testBytes, result);
        verify(awsService, times(1)).downloadFile(anyString(), anyString());
    }

    @Test
    public void getCommentImage_throwsCommentNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () ->
                commentService.getCommentImage(COMMENT_ID, Comment::getSmallImageFileKey));
    }

    @Test
    public void deleteCommentImageTest() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.deleteCommentImage(COMMENT_ID, AUTHOR_ID);

        verify(commentValidator, times(1)).validateAuthor(comment, AUTHOR_ID);
        verify(resourceService, times(2)).deleteResourceByKey(anyString());
        verify(awsService, times(2)).deleteFile(anyString(), anyString());
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment captured = commentCaptor.getValue();
        assertNull(captured.getSmallImageFileKey());
        assertNull(captured.getLargeImageFileKey());
    }

    @Test
    public void deleteCommentImage_throwsCommentNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () ->
                commentService.deleteCommentImage(COMMENT_ID, AUTHOR_ID));
    }
}
