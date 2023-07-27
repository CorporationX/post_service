package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private LikeDto likeDto;
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

        likeDto = new LikeDto(1L, 2L, comment, post);
    }

    @Test
    void createLikeOnPost() {
        Mockito.when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(userDto);

        long postId = likeDto.getPost().getId();
        Mockito.when(likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()))
                .thenReturn(Optional.of(Like.builder().build()));

        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        LikeDto likeOnPost = likeService.createLikeOnPost(likeDto);
        List<Like> actual = likeOnPost.getPost().getLikes();
        assertEquals(1, actual.size());
    }

    @Test
    void createLikeOnComment() {
        Mockito.when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(userDto);

        long commentId = likeDto.getComment().getId();
        Mockito.when(likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId()))
                .thenReturn(Optional.of(Like.builder().build()));

        Mockito.when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        LikeDto likeOnComment = likeService.createLikeOnComment(likeDto);
        List<Like> actual = likeOnComment.getComment().getLikes();
        assertEquals(1, actual.size());
    }
}