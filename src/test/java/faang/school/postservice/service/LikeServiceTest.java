package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.exception.SameTimeActionException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.redis.LikeEventPublisher;
import faang.school.postservice.validator.LikeValidator;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    LikeEventPublisher likeEventPublisher;
    @Mock
    private UserServiceClient userServiceClient;
    private LikeValidator likeValidator;
    private LikeDto likeDto;
    private UserDto userDto;
    LikeMapper likeMapper;
    PostMapper postMapper;

    @BeforeEach
    void setUp() {
        likeDto = LikeDto.builder().userId(1L).build();
        userDto = UserDto.builder().id(1L).username("Andrey").email("gmail@gmail.com").build();
        likeMapper = new LikeMapperImpl();
        postMapper = new PostMapperImpl();
        likeValidator = new LikeValidator(userServiceClient);
        likeService = new LikeService(likeValidator, likeMapper, likeRepository, postService, commentService,
                postMapper,likeEventPublisher);
    }

    @Test
    void testLikePost() throws JsonProcessingException {
        likeDto.setPostId(1L);

        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        PostDto post = PostDto.builder().id(1L).authorId(2L).build();
        Mockito.when(postService.getPost(1L)).thenReturn(post);

        Like like = Like.builder().id(0L).userId(1L).post(postMapper.toEntity(post)).build();

        assertEquals(likeMapper.toDto(like), likeService.likePost(likeDto));
        verify(likeRepository).save(like);
        verify(likeEventPublisher).publish(like);
    }

    @Test
    void testWhenUserDoesNotExistOnLikingPost() {
        Long userId = 1L;
        when(userServiceClient.getUser(userId)).thenThrow(FeignException.class);
        DataNotFoundException dataNotExistingException =
                assertThrows(DataNotFoundException.class, () -> likeService.likePost(likeDto));

        assertEquals(String.format("User with id=%d doesn't exist", userId), dataNotExistingException.getMessage());
    }

    @Test
    void testWhenAddLikeOnPostAndComment() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        likeDto.setCommentId(1L);
        likeDto.setPostId(1L);
        SameTimeActionException sameTimeActionException =
                assertThrows(SameTimeActionException.class, () -> likeService.likePost(likeDto));

        assertEquals("Can't add like on post and comment in the same time",
                sameTimeActionException.getMessage());
    }

    @Test
    void testUnlikePost() {
        likeService.unlikePost(1L, 1L);
        verify(likeRepository).deleteByPostIdAndUserId(1L, 1L);
    }

    @Test
    void testLikeComment() {
        likeDto.setCommentId(1L);

        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        Comment comment = Comment.builder().id(1L).build();
        when(commentService.getComment(1L)).thenReturn(comment);


        Like like = Like.builder().id(0L).userId(1L).comment(comment).build();

        assertEquals(likeMapper.toDto(like), likeService.likeComment(likeDto));
        verify(likeRepository).save(like);
    }

    @Test
    void testWhenUserDoesNotExistOnLikingComment() {
        Long userId = 1L;
        when(userServiceClient.getUser(userId)).thenThrow(FeignException.class);
        DataNotFoundException dataNotExistingException =
                assertThrows(DataNotFoundException.class, () -> likeService.likeComment(likeDto));

        assertEquals(String.format("User with id=%d doesn't exist", userId), dataNotExistingException.getMessage());
    }

    @Test
    void testUnlikeComment() {
        likeService.unlikeComment(1L, 1L);
        verify(likeRepository).deleteByCommentIdAndUserId(1L, 1L);
    }
}