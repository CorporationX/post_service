package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.ValidationServiceExceptions;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {


    @Mock
    private LikeMapper likeMapper;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    public void testCreateLikeToPost_Success() {
        LikeDto likeDto = LikeDto.builder()
                .userId(1L)
                .postId(1L)
                .build();
        Post post = new Post();
        post.setId(1L);
        post.setLikes(new ArrayList<>());

        var likeEntity = new Like();
        var likeDtoResponse = LikeDto.builder()
                .likeId(1L)
                .userId(1L)
                .postId(1L)
                .build();

        when(postRepository.existsById(likeDto.getPostId())).thenReturn(true);
        when(postRepository.findById(likeDto.getPostId())).thenReturn(Optional.of(post));
        when(likeMapper.toEntity(likeDto)).thenReturn(likeEntity);
        when(likeRepository.save(likeEntity)).thenReturn(likeEntity);
        when(likeMapper.toDto(likeEntity)).thenReturn(likeDtoResponse);

        LikeDto result = likeService.createLikeToPost(likeDto);

        assertNotNull(result);
        assertEquals(likeDtoResponse, result);
        verify(postRepository).save(post);
    }

    @Test
    public void testCreateLikeToPost_PostNotFound() {
        LikeDto likeDto = LikeDto.builder()
                .userId(1L)
                .postId(1L)
                .build();

        when(postRepository.existsById(likeDto.getPostId())).thenReturn(false);

        ValidationServiceExceptions exception = assertThrows(
                ValidationServiceExceptions.class,
                () -> likeService.createLikeToPost(likeDto)
        );

        assertTrue(exception.getMessage().contains("no such postId exists postId: 1"));
    }

    @Test
    public void testRemoveLikeToPost_Success() {
        // Arrange
        LikeDto likeDto = LikeDto.builder()
                .likeId(1L)
                .userId(1L)
                .postId(1L)
                .build();

        Post post = new Post();
        post.setId(1L);
        post.setLikes(new ArrayList<>());

        Like likeEntity = new Like();
        likeEntity.setId(1L);
        likeEntity.setPost(post);
        post.getLikes().add(likeEntity);

        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(true);
        when(postRepository.existsById(likeDto.getPostId())).thenReturn(true);
        when(postRepository.findById(likeDto.getPostId())).thenReturn(Optional.of(post));
        when(likeRepository.findById(likeDto.getLikeId())).thenReturn(Optional.of(likeEntity));
        when(likeMapper.toEntity(likeDto)).thenReturn(likeEntity);

        LikeDto result = likeService.removeLikeToPost(likeDto);

        assertEquals(likeDto, result);
        verify(postRepository).save(post);
        verify(likeRepository).delete(likeEntity);
    }

    @Test
    public void testRemoveLikeToPost_LikeNotFound() {
        LikeDto likeDto = LikeDto.builder()
                .likeId(1L)
                .userId(1L)
                .postId(1L)
                .build();

        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(false);

        ValidationServiceExceptions exception = assertThrows(
                ValidationServiceExceptions.class,
                () -> likeService.removeLikeToPost(likeDto)
        );

        assertTrue(exception.getMessage().contains("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: 1"));
    }

    @Test
    public void testCreateLikeToComment_Success() {
        LikeDto likeDto = LikeDto.builder()
                .userId(1L)
                .commentId(1L)
                .build();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setLikes(new ArrayList<>());
        var likeEntity = new Like();
        var likeDtoResponse = LikeDto.builder()
                .likeId(1L)
                .userId(1L)
                .commentId(1L)
                .build();

        when(commentRepository.existsById(likeDto.getCommentId())).thenReturn(true);
        when(commentRepository.findById(likeDto.getCommentId())).thenReturn(Optional.of(comment));
        when(likeMapper.toEntity(likeDto)).thenReturn(likeEntity);
        when(likeRepository.save(likeEntity)).thenReturn(likeEntity);
        when(likeMapper.toDto(likeEntity)).thenReturn(likeDtoResponse);

        LikeDto result = likeService.createLikeToComment(likeDto);

        assertNotNull(result);
        assertEquals(likeDtoResponse, result);
        verify(commentRepository).save(comment);
    }

    @Test
    public void testCreateLikeToComment_CommentNotFound() {
        LikeDto likeDto = LikeDto.builder()
                .userId(1L)
                .commentId(1L)
                .build();

        when(commentRepository.existsById(likeDto.getCommentId())).thenReturn(false);

        ValidationServiceExceptions exception = assertThrows(
                ValidationServiceExceptions.class,
                () -> likeService.createLikeToComment(likeDto)
        );

        assertTrue(exception.getMessage().contains("no such postId exists commentId: 1"));
    }

    @Test
    public void testRemoveLikeToComment_Success() {
        LikeDto likeDto = LikeDto.builder()
                .likeId(1L)
                .userId(1L)
                .commentId(1L)
                .build();
        Comment comment = new Comment();
        comment.setId(1L);
        Like likeEntity = new Like();
        likeEntity.setId(likeDto.getLikeId());
        likeEntity.setUserId(likeDto.getUserId());
        likeEntity.setComment(comment);

        comment.setLikes(new ArrayList<>());
        comment.getLikes().add(likeEntity);  // Добавляем лайк в комментарий

        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(true);
        when(commentRepository.existsById(likeDto.getCommentId())).thenReturn(true);
        when(commentRepository.findById(likeDto.getCommentId())).thenReturn(Optional.of(comment));
        when(likeRepository.findById(likeDto.getLikeId())).thenReturn(Optional.of(likeEntity));
        when(likeMapper.toEntity(likeDto)).thenReturn(likeEntity);

        LikeDto result = likeService.removeLikeToComment(likeDto);

        assertEquals(likeDto, result);
        verify(commentRepository).save(comment);
        verify(likeRepository).delete(likeEntity);
    }

    @Test
    public void testRemoveLikeToComment_LikeNotFound() {
        LikeDto likeDto = LikeDto.builder()
                .likeId(1L)
                .userId(1L)
                .commentId(1L)
                .build();

        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(false);

        assertThrows(ValidationServiceExceptions.class, () ->
                likeService.removeLikeToComment(likeDto));
    }

    @Test
    public void testValidationAndPostReceived_PostNotFound() {
        Long postId = 1L;

        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(ValidationServiceExceptions.class, () ->
                likeService.createLikeToPost(new LikeDto(null, null, null, postId)));
    }

    @Test
    public void testValidationAndCommentsReceived_CommentNotFound() {
        Long commentId = 1L;
        var likeDto = new LikeDto(1L, null, commentId, null);
        when(commentRepository.existsById(commentId)).thenReturn(false);
        when(likeRepository.existsById(likeDto.getLikeId())).thenReturn(true);

        assertThrows(ValidationServiceExceptions.class, () ->
                likeService.removeLikeToComment(likeDto));
    }

    @Test
    public void testValidationAndCommentsReceived_BothIdsNull() {
        assertThrows(ValidationServiceExceptions.class, () ->
                likeService.removeLikeToComment(new LikeDto(null, null, null, null)));
    }
}
