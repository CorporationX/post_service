package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Spy
    private LikeMapper likeMapper = LikeMapper.INSTANCE;
    @Spy
    private ResponsePostMapper postMapper = ResponsePostMapper.INSTANCE;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private LikeService likeService;
    private LikeDto likeDto;
    private ResponsePostDto postDto;
    private UserDto userDto;
    private Post post;
    private Like like;
    private Comment comment;
    private CommentDto commentDto;
    private LikeDto likeDto1;
    private LikeDto likeDto2;
    private LikeDto likeDto3;

    @BeforeEach
    public void setUp() {
        likeDto1 =LikeDto.builder().id(2L).build();
        likeDto2 =LikeDto.builder().id(3L).build();
        likeDto3 =LikeDto.builder().id(6L).build();
        userDto = UserDto.builder()
                .id(1L)
                .username("Ser").build();
        commentDto = CommentDto.builder().id(2L).postDto(postDto).authorId(userDto.getId()).build();
        postDto = ResponsePostDto.builder()
                .id(3L)
                .published(true)
                .likes(List.of(likeDto1, likeDto2, likeDto3))
                .build();
        likeDto = LikeDto.builder()
                .id(5L)
                .commentId(commentDto.getId())
                .commentDto(commentDto)
                .userDto(userDto)
                .userId(userDto.getId())
                .postId(postDto.getId()).build();
        post = Post.builder().id(3L).likes(List.of(new Like(), new Like(), new Like())).build();
        like = Like.builder().id(5L).post(post).build();

        comment = Comment.builder().id(2L).post(post).authorId(userDto.getId()).build();
    }

    @Test
    public void likePost_correctAnswer() {
        when(userServiceClient.getUser(1L)).thenReturn(likeDto.getUserDto());
        when(postService.getById(3L)).thenReturn(postDto);
        when(likeMapper.toLike(likeDto)).thenReturn(like);
        when(likeRepository.save(any())).thenReturn(like);
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);
        LikeDto likeDto1 = likeService.likePost(likeDto);
        assertEquals(likeDto, likeDto1);
    }

    @Test
    public void deleteLikePost_correctAnswer() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        likeService.deleteLikePost(3L, 1L);
        verify(likeRepository).deleteByPostIdAndUserId(3L, 1L);
    }

    @Test
    public void likeComment_correctAnswer() {
        when(userServiceClient.getUser(1L)).thenReturn(likeDto.getUserDto());
        when(commentService.getCommentById(2L)).thenReturn(commentDto);
        when(likeMapper.toLike(likeDto)).thenReturn(like);
        when(likeRepository.save(any())).thenReturn(like);
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);
        LikeDto likeDto1 = likeService.likeComment(likeDto);
        assertEquals(likeDto, likeDto1);
    }

    @Test
    public void deleteLikeComment_correctAnswer() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        likeService.deleteLikeComment(2L, 1L);
        verify(likeRepository).deleteByCommentIdAndUserId(2L, 1L);
    }

    @Test
    public void getAllLikesByPost_correctAnswer() {
        //when(likeMapper.toDtoList(post.getLikes())).thenReturn(postDto.getLikes());
        when(postService.getById(3L)).thenReturn(postDto);
        when(likeRepository.findByPostId(3L)).thenReturn(post.getLikes());

        assertEquals(3, likeService.getAllLikesByPost(3L));
    }
}