package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.*;
import faang.school.postservice.messaging.likeevent.LikeEventPublisher;
import faang.school.postservice.messaging.likeevent.LikeProducer;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private LikeMapperImpl likeMapper;

    @Mock
    private LikeEventPublisher likeEventPublisher;

    @Mock
    private LikeProducer likeProducer;

    private LikeDto likeDto;
    private Like like;
    private UserDto userDto;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(3L);
        post.setLikes(new ArrayList<>());

        comment = new Comment();
        comment.setId(5L);
        comment.setLikes(new ArrayList<>());

        likeDto = new LikeDto(1L, 2L, comment.getId(), post.getId());
        like = likeMapper.toEntity(likeDto);

        likeService.setBATCH_SIZE(100);
    }

    @Test
    void createLikeOnPost() {
        Mockito.when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(userDto);

        long postId = likeDto.getPostId();
        Mockito.when(likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()))
                .thenReturn(Optional.empty());

        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));


        LikeDto likeOnPost = likeService.createLikeOnPost(likeDto);
        assertNotNull(likeOnPost);
        assertEquals(likeDto.getPostId(), likeOnPost.getPostId());

        Mockito.verify(likeRepository).save(Mockito.any());
        Mockito.verify(likeEventPublisher).publish(Mockito.any());
        Mockito.verify(likeProducer).publish(Mockito.any());
    }

    @Test
    void createExistLikeOnPost() {
        Mockito.when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(userDto);

        long postId = likeDto.getPostId();
        Like expectedLike = Like.builder().id(likeDto.getId())
                .userId(likeDto.getUserId()).comment(comment).post(post).build();

        Mockito.when(likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()))
                .thenReturn(Optional.of(expectedLike));

        LikeDto actualLike = likeService.createLikeOnPost(likeDto);
        assertNotNull(actualLike);
        assertEquals(expectedLike.getId(), actualLike.getId());
    }

    @Test
    void createLikeOnComment() {
        Mockito.when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(userDto);

        long commentId = likeDto.getCommentId();
        Mockito.when(likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId()))
                .thenReturn(Optional.empty());

        Mockito.when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        LikeDto likeOnComment = likeService.createLikeOnComment(likeDto);
        assertNotNull(likeOnComment);
        assertEquals(commentId, likeOnComment.getCommentId());

        Mockito.verify(likeRepository).save(Mockito.any());
    }

    @Test
    void createExistLikeOnComment() {
        Mockito.when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(userDto);

        long commentId = likeDto.getCommentId();
        Like expectedLike = Like.builder().id(likeDto.getId())
                .userId(likeDto.getUserId()).comment(comment).post(post).build();

        Mockito.when(likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId()))
                .thenReturn(Optional.of(expectedLike));


        LikeDto actualLike = likeService.createLikeOnComment(likeDto);
        assertNotNull(actualLike);
        assertEquals(expectedLike.getId(), actualLike.getId());
    }

    @Test
    void deleteLikeOnPost() {
        Mockito.when(postRepository.existsById(3L)).thenReturn(true);
        likeService.deleteLikeOnPost(likeDto);
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId());
    }

    @Test
    void deleteLikeOnComment() {
        Mockito.when(commentRepository.existsById(5L)).thenReturn(true);
        likeService.deleteLikeOnComment(likeDto);
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId());
    }

    @Test
    void deleteLikeOnPost_EntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> likeService.deleteLikeOnPost(likeDto));
    }

    @Test
    void deleteLikeOnComment_EntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> likeService.deleteLikeOnComment(likeDto));
    }

    @Test
    void getAllPostLikes() {
        Mockito.when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(userDto);

        List<Like> likes = List.of(likeMapper.toEntity(likeDto));
        Mockito.when(likeRepository.findByPostId(likeDto.getPostId()))
                .thenReturn(likes);

        List<LikeDto> expected = List.of(likeDto);
        List<LikeDto> actual = likeService.getAllPostLikes(likeDto);

        assertEquals(expected, actual);
    }

    @Test
    void getUsersLikeFromPost_Successful(){
        Mockito.when(likeRepository.findByPostId(1L))
                .thenReturn(List.of(Like
                        .builder()
                        .userId(1L)
                        .build()));

        likeService.getUsersLikeFromPost(1L);

        Mockito.verify(likeRepository).findByPostId(1L);
    }

    @Test
    void getUsersLikeFromComment_Successful(){
        Mockito.when(likeRepository.findByCommentId(1L))
                .thenReturn(List.of(Like
                        .builder()
                        .userId(1L)
                        .build()));

        likeService.getUsersLikeFromComment(1L);

        Mockito.verify(likeRepository).findByCommentId(1L);
    }
}