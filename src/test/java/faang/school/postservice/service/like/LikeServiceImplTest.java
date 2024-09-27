package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.validator.ServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @InjectMocks
    LikeServiceImpl likeService;

    @Mock
    PostRepository postRepository;

    @Mock
    LikeRepository likeRepository;

    @Spy
    LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);

    @Mock
    CommentRepository commentRepository;

    @Mock
    ServiceValidator validator;

    @Mock
    UserServiceClient userServiceClient;


    LikeDto likeDto = new LikeDto();
    Post post = new Post();
    Comment comment = new Comment();
    Like like = new Like();

    @BeforeEach
    void setUp() {
        likeDto.setPostId(1L);
        likeDto.setUserId(1L);
        likeDto.setCommentId(1L);
        likeDto.setCreatedAt(LocalDateTime.now());

        post.setId(1L);
        post.setComments(List.of(comment));
        post.setLikes(new ArrayList<>());

        comment.setId(1L);
        comment.setPost(post);
        comment.setLikes(new ArrayList<>());

        like.setId(1L);
        like.setPost(post);
        like.setComment(comment);
        like.setCreatedAt(LocalDateTime.now());
    }


    @Test
    void testLikePost() {
        when(userServiceClient.getUser(likeDto.getUserId())).thenReturn(new UserDto(1L, "name", "email"));
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(postRepository.findById(likeDto.getPostId())).thenReturn(Optional.of(post));
        when(likeRepository.findByPostId(likeDto.getPostId())).thenReturn(List.of());

        LikeDto result = likeService.likePost(likeDto);

        assertEquals(like.getId(), result.getId());
        verify(likeRepository).save(like);
    }

    @Test
    void testUnlikePost() {
        likeRepository.deleteByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId());
        verify(likeRepository).deleteByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId());
    }

    @Test
    void testLikeComment() {
        when(userServiceClient.getUser(likeDto.getUserId())).thenReturn(new UserDto(1L, "name", "email"));
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(postRepository.findById(likeDto.getPostId())).thenReturn(Optional.of(post));
        when(commentRepository.findById(likeDto.getCommentId())).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentId(likeDto.getCommentId())).thenReturn(List.of());

        LikeDto result = likeService.likeComment(likeDto);

        assertEquals(like.getId(), result.getId());
        verify(likeRepository).save(like);
    }

    @Test
    void testUnlikeComment() {
        likeRepository.deleteByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId());
        verify(likeRepository).deleteByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId());
    }
}