package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeServiceImpl;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @InjectMocks
    private LikeServiceImpl likeService;

    @Mock
    public PostRepository postRepository;

    @Mock
    public CommentRepository commentRepository;

    @Spy
    public LikeMapperImpl likeMapperImpl;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeEventPublisher likeEventPublisher;

    private List<Like> likes1;
    private List<Like> likes2;
    private Post post;
    private Comment comment;
    private Like like1;
    private LikeDto likeDto;
    private long postId;
    private long commentId;
    private List<UserDto> userDtos;
    private final long POST_ID = 1L;

    @BeforeEach
    void init() {
        post = new Post();
        like1 = new Like();
        comment = new Comment();
        likeDto = new LikeDto(1L, 2L);
        postId = 3L;
        commentId = 3L;
        likes1 = new ArrayList<>();
        likes2 = new ArrayList<>();
        List<Like> likes = new ArrayList<>();
        userDtos = new ArrayList<>();


        for (int i = 1; i < 150; i++) {
            Like like = new Like();
            like.setUserId((long) i);
            likes.add(like);

            UserDto userDto = new UserDto();
            userDto.setId((long) i);
            userDtos.add(userDto);
        }

        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        Mockito.lenient().when(likeRepository.findByPostId(POST_ID))
                .thenReturn(likes);
        Mockito.lenient().when(likeRepository.findByCommentId(POST_ID))
                .thenReturn(likes);
        Mockito.lenient().when(userServiceClient.getUsersByIds(userIds.subList(0, 100)))
                .thenReturn(userDtos.subList(0, 100));
        Mockito.lenient().when(userServiceClient.getUsersByIds(userIds.subList(100, 149)))
                .thenReturn(userDtos.subList(100, 149));
    }

    @Test
    void getUsersLikedPost_whenOk() {
        Assertions.assertEquals(likeService.getUsersLikedPost(POST_ID), userDtos);
    }

    @Test
    void getUsersLikedComm_whenOk() {
        Assertions.assertEquals(likeService.getUsersLikedComm(POST_ID), userDtos);
    }


    @Test
    void addLikeToPost() {
        like1.setId(1L);
        like1.setUserId(2L);
        post.setId(postId);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeMapperImpl.toLike(likeDto)).thenReturn(like1);
        doNothing().when(likeEventPublisher).publish(any(LikeEvent.class));
        when(likeRepository.save(any(Like.class))).thenReturn(like1);

        likeService.addLikeToPost(likeDto, postId);

        verify(likeEventPublisher, times(1)).publish(any(LikeEvent.class));
        verify(likeRepository, times(1)).save(like1);
        verify(userServiceClient, times(1)).getUser(like1.getUserId());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void deleteLikeFromPostThatNotLiked() {
        like1.setId(1L);
        like1.setUserId(2L);
        post.setLikes(likes1);
        post.setId(postId);
        like1.setPost(post);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> likeService.deleteLikeFromPost(likeDto, postId));
    }

    @Test
    void deleteLikeFromPostSuccessful() {
        likes1.add(like1);
        like1.setId(1L);
        like1.setUserId(2L);
        post.setLikes(likes1);
        post.setId(postId);
        like1.setPost(post);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeMapperImpl.toLike(likeDto)).thenReturn(like1);

        assertDoesNotThrow(() -> likeService.deleteLikeFromPost(likeDto, postId));
    }

    @Test
    void addLikeToComment() {
        likes1.add(like1);
        like1.setId(1L);
        like1.setUserId(2L);
        post.setId(commentId);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        comment.setPost(post);
        post.setComments(List.of(comment));
        comment.setLikes(new ArrayList<>());
        comment.setId(commentId);
        like1.setComment(comment);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        likeService.addLikeToComment(likeDto, commentId);

        verify(likeRepository, times(1)).save(like1);
    }

    @Test
    void deleteLikeFromNotLikedComment() {
        likes1.add(like1);
        likes2.add(new Like());
        comment.setPost(post);
        comment.setId(commentId);
        comment.setLikes(likes2);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(DataValidationException.class, () -> likeService.deleteLikeFromComment(likeDto, commentId));
    }

    @Test
    void deleteLikeFromComment() {
        likes1.add(like1);
        likes2.add(new Like());
        comment.setPost(post);
        comment.setId(commentId);
        comment.setLikes(new ArrayList<>(likes1));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeMapperImpl.toLike(likeDto)).thenReturn(like1);

        assertDoesNotThrow(() -> likeService.deleteLikeFromComment(likeDto, commentId));
    }
}

