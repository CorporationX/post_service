package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.mapper.post.ResponsePostMapper;
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
    @InjectMocks
    private LikeService likeService;
    private LikeDto likeDto;
    private ResponsePostDto postDto;
    private UserDto userDto;
    private Post post;
    private Like like;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .username("Ser").build();
        postDto = ResponsePostDto.builder()
                .id(3L)
                .likesIds(List.of(1L, 2L, 3L))
                .published(true).build();
        likeDto = LikeDto.builder()
                .id(5L)
                .userDto(userDto)
                .userId(userDto.getId())
                .postId(postDto.getId()).build();
        post = Post.builder().id(3L).likes(List.of(new Like(), new Like(), new Like())).build();
        like = Like.builder().id(5L).post(post).build();
    }

    @Test
    public void likePost_correctAnswer() {
        when(userServiceClient.getUser(1L)).thenReturn(likeDto.getUserDto());
        when(postService.getById(3L)).thenReturn(postDto);
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.save(any())).thenReturn(like);
        when(likeMapper.toDto(like)).thenReturn(likeDto);
        LikeDto likeDto1 = likeService.likePost(likeDto);
        assertEquals(likeDto, likeDto1);
    }

    @Test
    public void deleteLikePost_correctAnswer() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        likeService.deleteLikePost(3L, 1L);
        verify(likeRepository).deleteByPostIdAndUserId(3L, 1L);
    }
}