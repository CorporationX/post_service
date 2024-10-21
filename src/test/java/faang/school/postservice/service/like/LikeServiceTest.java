package faang.school.postservice.service.like;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.FeedEventService;
import faang.school.postservice.service.publisher.LikeEventPublisher;
import faang.school.postservice.validator.like.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeEventPublisher eventPublisher;
    @Mock
    private FeedEventService feedEventService;

    LikeDto likeDto;
    private Post post;
    private Comment comment;
    private Like like;

    @InjectMocks
    private LikeService likeService;

    // merged with `@Vingerri`

    private Long postId;
    private Long likeId;
    Like firstLike;
    Like secondLike;
    List<Like> likes;
    UserDto userOne;
    UserDto userTwo;
    List<UserDto> users;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(likeService, "userBatchSize", 100);
        likeDto = new LikeDto();
        likeDto.setUserId(1L);
        likeDto.setPostId(1L);
        likeDto.setCommentId(1L);

        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setId(1L);

        like = new Like();
        like.setId(1L);
        like.setUserId(1L);
        like.setPost(post);
        like.setComment(comment);

        // merged with `@Vingerri`

        postId = 1L;
        likeId = 2L;
        firstLike = Like.builder().id(1L).userId(1L).build();
        secondLike = Like.builder().id(2L).userId(2L).build();
        likes = List.of(firstLike, secondLike);
        userOne = new UserDto(1L, "first", "mail.one");
        userTwo = new UserDto(2L, "second", "mail.two");
        users = List.of(userOne, userTwo);
    }


    @Test
    void testLikePost() {
        post.setAuthorId(10L);
        post.setId(100L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        likeService.likePost(likeDto);

        verify(likeRepository).save(like);
    }

    @Test
    void testUnlikePost() {
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.of(like));
        likeService.unlikePost(likeDto);
        verify(likeRepository).delete(like);
    }

    @Test
    void testUnlikePostNotFound() {
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> likeService.unlikePost(likeDto));
        assertEquals("Лайк не найден", exception.getMessage());
    }

    @Test
    void testLikeComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        likeService.likeComment(likeDto);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    void testUnlikeComment() {
        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.of(like));
        likeService.unlikeComment(likeDto);
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void testUnlikeCommentNotFound() {
        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> likeService.unlikeComment(likeDto));
        assertEquals("Лайк не найден", exception.getMessage());
    }

    // merged with `@Vingerri`

    @Test
    public void getUsersThatLikedPostTest() {
        Mockito.when(likeRepository.findByPostId(postId)).thenReturn(likes);
        Mockito.when(userServiceClient.getUsersByIds(Mockito.anyList())).thenReturn(users);
        List<UserDto> actualUsers = likeService.getUsersThatLikedPost(postId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostId(1L);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUsersByIds(Mockito.anyList());
        Assert.assertEquals(actualUsers, users);
    }

    @Test
    public void getUsersThatLikedCommentTest() {
        Mockito.when(likeRepository.findByCommentId(likeId)).thenReturn(likes);
        Mockito.when(userServiceClient.getUsersByIds(Mockito.anyList())).thenReturn(users);
        List<UserDto> actualUsers = likeService.getUsersThatLikedComment(likeId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentId(likeId);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUsersByIds(Mockito.anyList());
        Assert.assertEquals(actualUsers, users);
    }

    @Test
    public void deleteLikeFromNonExistentPostTest() {
        Mockito.when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class, () -> {
            likeService.deleteLikeFromPost(1L, 1L);
        });
    }

    @Test
    public void deleteLikeFromCommentTest() {
        Mockito.when(likeRepository.findByCommentIdAndUserId(2L, 2L)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class, () -> {
            likeService.deleteLikeFromComment(2L, 2L);
        });
    }
}
