package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentResponseImageDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.amazonS3.ImageCompressor;
import faang.school.postservice.service.amazonS3.S3Service;
import faang.school.postservice.validation.comment.CommentValidation;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final long COMMENT_ID = 1L;
    private static final long POST_ID = 2L;
    private static final long USER_ID = 3L;
    private static final String CONTENT = "content";
    private static final String KEY_LARGE_IMAGE = "largeKey";
    private static final String KEY_SMALL_IMAGE = "smallKey";
    private static final int MAX_SIZE_FOR_LARGE_IMAGE = 1080;
    private static final int MAX_SIZE_FOR_SMALL_IMAGE = 170;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserContext userContext;

    @Mock
    private PostRepository postRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageCompressor imageCompressor;

    @Mock
    private MultipartFile originalFile;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Comment commentSaved;
    private UserDto userDto;
    private Post post;


    @BeforeEach
    void setUp() {
        CommentValidation commentValidation = new CommentValidation(postRepository);
        commentService = CommentService.builder()
                .userServiceClient(userServiceClient)
                .commentRepository(commentRepository)
                .commentValidation(commentValidation)
                .userContext(userContext)
                .postRepository(postRepository)
                .s3Service(s3Service)
                .imageCompressor(imageCompressor)
                .build();

        post = new Post();
        post.setId(POST_ID);

        commentSaved = Comment.builder()
                .id(COMMENT_ID)
                .authorId(USER_ID)
                .post(post)
                .content(CONTENT)
                .build();

        comment = Comment.builder()
                .authorId(USER_ID)
                .post(post)
                .content(CONTENT)
                .build();
        userDto = new UserDto(USER_ID, "User", "user@mail.ru");
    }

    @Test
    void testCreateCommentWhenCommentCreated() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(commentRepository.save(comment)).thenReturn(commentSaved);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

        Comment result = commentService.createComment(POST_ID, CONTENT);

        verify(commentRepository).save(commentCaptor.capture());
        assertEquals(COMMENT_ID, result.getId());
        assertEquals(commentSaved.getAuthorId(), commentCaptor.getValue().getAuthorId());
        verify(userContext).getUserId();
        verify(postRepository).findById(POST_ID);
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    void testCreateCommentWhenPostNotFound() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(POST_ID, CONTENT));
        verify(userContext).getUserId();
        verify(postRepository).findById(POST_ID);
        verify(userServiceClient, never()).getUser(USER_ID);
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void testUpdateCommentWhenCommentUpdated() {
        String otherContent = CONTENT + "other";
        Comment commentNewContent = comment;
        commentNewContent.setId(COMMENT_ID);
        commentNewContent.setContent(otherContent);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentSaved));
        when(commentRepository.save(commentSaved)).thenReturn(commentNewContent);

        Comment result = commentService.updateComment(COMMENT_ID, otherContent);

        assertEquals(commentSaved.getId(), result.getId());
        assertEquals(commentSaved.getContent(), result.getContent());
        verify(userContext).getUserId();
        verify(commentRepository).save(commentCaptor.capture());
    }

    @Test
    void testGetAllCommentWhenPostExists() {
        List<Comment> comments = List.of(comment, commentSaved);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<Comment> result = commentService.getAllComments(POST_ID);

        assertEquals(comments.get(0).getId(), result.get(0).getId());
        assertEquals(comments.get(1).getId(), result.get(1).getId());
        assertEquals(comments.size(), result.size());
        verify(commentRepository).findAllByPostId(POST_ID);
    }


    @Test
    void testGetAllCommentWhenListCommentsEmpty() {
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(Collections.emptyList());
        when(postRepository.existsById(POST_ID)).thenReturn(true);

        List<Comment> result = commentService.getAllComments(POST_ID);

        assertEquals(Collections.emptyList(), result);
        verify(commentRepository).findAllByPostId(POST_ID);
        verify(postRepository).existsById(POST_ID);
    }

    @Test
    void testDeleteCommentWhenCommentExists() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentSaved));
        doNothing().when(commentRepository).deleteById(COMMENT_ID);

        assertDoesNotThrow(() -> commentService.deleteComment(COMMENT_ID));
        verify(userContext).getUserId();
        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void testGetCommentWhenCommentNotExists() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> commentService.getComment(COMMENT_ID));
    }

    @Test
    void testUploadFileWhenFileUploaded() {
        MultipartFile compressedLargeFile = mock(MultipartFile.class);
        MultipartFile compressedSmallFile = mock(MultipartFile.class);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentSaved));
        when(imageCompressor.compressImage(originalFile, MAX_SIZE_FOR_LARGE_IMAGE)).thenReturn(compressedLargeFile);
        when(imageCompressor.compressImage(originalFile, MAX_SIZE_FOR_SMALL_IMAGE)).thenReturn(compressedSmallFile);
        when(s3Service.uploadFile(contains("largeImageForComment"), any(MultipartFile.class))).thenReturn(KEY_LARGE_IMAGE);
        when(s3Service.uploadFile(contains("SmallImageForComment"), any(MultipartFile.class))).thenReturn(KEY_SMALL_IMAGE);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

        commentService.uploadFile(COMMENT_ID, originalFile);

        verify(commentRepository).findById(COMMENT_ID);
        verify(imageCompressor, times(2)).compressImage(any(MultipartFile.class), anyInt());
        verify(s3Service).uploadFile(contains("largeImageForComment"), eq(compressedLargeFile));
        verify(s3Service).uploadFile(contains("SmallImageForComment"), eq(compressedSmallFile));
        verify(commentRepository).save(commentCaptor.capture());
        Comment savedComment = commentCaptor.getValue();
        assertEquals(KEY_LARGE_IMAGE, savedComment.getLargeImageFileKey());
        assertEquals(KEY_SMALL_IMAGE, savedComment.getSmallImageFileKey());
    }

    @Test
    void testDeleteFileWhenFileDeleted() {
        commentSaved.setLargeImageFileKey(KEY_LARGE_IMAGE);
        commentSaved.setSmallImageFileKey(KEY_SMALL_IMAGE);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentSaved));
        doNothing().when(s3Service).deleteFile(KEY_LARGE_IMAGE);
        doNothing().when(s3Service).deleteFile(KEY_SMALL_IMAGE);

        commentService.deleteFile(COMMENT_ID);

        verify(s3Service).deleteFile(KEY_LARGE_IMAGE);
        verify(s3Service).deleteFile(KEY_SMALL_IMAGE);
    }

    @Test
    void testDownloadLargeImageWhenFileDownloaded() {
        commentSaved.setLargeImageFileKey(KEY_LARGE_IMAGE);
        CommentResponseImageDto commentResponseImageDto = new CommentResponseImageDto();
        commentResponseImageDto.setFileName("ResponseDto");

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentSaved));
        when(s3Service.downloadFile(KEY_LARGE_IMAGE)).thenReturn(commentResponseImageDto);

        CommentResponseImageDto result = commentService.downloadLargeImage(COMMENT_ID);

        assertEquals(result.getFileName(), commentResponseImageDto.getFileName());
        verify(s3Service).downloadFile(KEY_LARGE_IMAGE);
    }

    @Test
    void testDownloadSmallImageWhenFileDownloaded() {
        commentSaved.setLargeImageFileKey(KEY_SMALL_IMAGE);
        CommentResponseImageDto commentResponseImageDto = new CommentResponseImageDto();
        commentResponseImageDto.setFileName("ResponseDto");

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentSaved));
        when(s3Service.downloadFile(KEY_SMALL_IMAGE)).thenReturn(commentResponseImageDto);

        CommentResponseImageDto result = commentService.downloadLargeImage(COMMENT_ID);

        assertEquals(result.getFileName(), commentResponseImageDto.getFileName());
        verify(s3Service).downloadFile(KEY_SMALL_IMAGE);

    }


}
