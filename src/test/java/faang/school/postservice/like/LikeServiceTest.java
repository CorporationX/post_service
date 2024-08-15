package faang.school.postservice.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.like.LikeServiceImpl;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static faang.school.postservice.util.TestDataFactory.ID;
import static faang.school.postservice.util.TestDataFactory.INVALID_ID;
import static faang.school.postservice.util.TestDataFactory.getUserDtoList;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private LikeEventPublisher likePublisher;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private PostMapper postMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    UserServiceClient userServiceClient;

    @InjectMocks
    private LikeServiceImpl likeService;

    private LikeDto likeDto;
    private Like like;
    private Post post;
    private Comment comment;
    private PostDto postDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        likeDto = new LikeDto();
        likeDto.setUserId(1L);
        likeDto.setPostId(1L);
        likeDto.setCommentId(1L);

        like = new Like();
        like.setId(1L);

        List<Like> likes = new ArrayList<>();
        likes.add(like);
        likes.add(like);
        likes.add(like);

        post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setLikes(likes);

        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .build();

        comment = new Comment();
        comment.setId(1L);
        comment.setAuthorId(1L);
        comment.setLikes(likes);
        likeService.setBatchSize(100);
    }

    @Test
    void addPostLike() {
        when(postService.getPost(anyLong())).thenReturn(postDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(likeMapper.toEntity(any(LikeDto.class))).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toDto(any(Like.class))).thenReturn(likeDto);

        LikeDto result = likeService.addPostLike(likeDto);

        verify(likeValidator).validateUserExistence(likeDto.getUserId());
        verify(likeValidator).validateLikeToPost(post, likeDto.getUserId());
        verify(likeRepository).save(like);
        verify(likePublisher).publish(any(LikeEvent.class));

        assertEquals(likeDto, result);
    }

    @Test
    void deletePostLike() {
        when(postService.getPost(anyLong())).thenReturn(postDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(likeMapper.toEntity(any(LikeDto.class))).thenReturn(like);

        likeService.deletePostLike(likeDto);

        verify(likeRepository).deleteByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId());
    }

    @Test
    void addCommentLike() {
        when(commentService.getComment(anyLong())).thenReturn(commentDto);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(likeMapper.toEntity(any(LikeDto.class))).thenReturn(like);
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(likeMapper.toDto(any(Like.class))).thenReturn(likeDto);

        LikeDto result = likeService.addCommentLike(likeDto);

        verify(likeValidator).validateUserExistence(likeDto.getUserId());
        verify(likeValidator).validateLikeToComment(comment, likeDto.getUserId());
        verify(likeRepository).save(like);
        verify(likePublisher).publish(any(LikeEvent.class));

        assertEquals(likeDto, result);
    }

    @Test
    void deleteCommentLike() {
        when(commentService.getComment(anyLong())).thenReturn(commentDto);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(likeMapper.toEntity(any(LikeDto.class))).thenReturn(like);

        likeService.deleteCommentLike(likeDto);

        verify(likeRepository).deleteByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId());
    }



    @Test
    void givenPostIdWhenFindUsersByPostIdThenReturnUsers() {
        // given - precondition
        List<Like> likeList = of(new Like(), new Like());
        List<UserDto> userDtoList = getUserDtoList();

        when(likeRepository.findByPostId(ID))
                .thenReturn(likeList);
        when(userServiceClient.getUsersByIds(anyList()))
                .thenReturn(userDtoList);

        // when - action
        var actualResult = likeService.findUsersByPostId(ID);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveFieldByFieldElementComparator()
                .containsAnyElementsOf(userDtoList);
    }

    @Test
    void givenInvalidPostIdWhenFindUsersByPostIdThenThrowException() {
        // given - precondition
        when(likeRepository.findByPostId(anyLong()))
                .thenReturn(emptyList());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> likeService.findUsersByPostId(INVALID_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No users found for ID " + INVALID_ID);
    }

    @Test
    void givenCommentIdWhenFindUsersByPostIdThenReturnUsers() {
        // given - precondition
        List<Like> likeList = of(new Like(), new Like());
        List<UserDto> userDtoList = getUserDtoList();

        when(likeRepository.findByCommentId(anyLong()))
                .thenReturn(likeList);
        when(userServiceClient.getUsersByIds(anyList()))
                .thenReturn(userDtoList);

        // when - action
        var actualResult = likeService.findUsersByCommentId(ID);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveFieldByFieldElementComparator()
                .containsAnyElementsOf(userDtoList);
    }
    @Test
    void givenInvalidCommentIdWhenFindUsersByPostIdThenThrowException() {
        // given - precondition
        when(likeRepository.findByCommentId(anyLong()))
                .thenReturn(emptyList());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> likeService.findUsersByCommentId(INVALID_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No users found for ID " + INVALID_ID);
    }
}
