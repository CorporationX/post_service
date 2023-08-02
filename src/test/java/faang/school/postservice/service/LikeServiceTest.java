package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    @Spy
    private LikeMapperImpl likeMapper;
    @InjectMocks
    private LikeService likeService;
    private LikeDto likeDto;
    private Like like;
    private final Long CURRENT_USER_ID = 20L;
    private final Long POST_ID = 30L;
    private final Long COMMENT_ID = 40L;
    private Post post;
    private Comment comment;
    private UserDto userDto;

    @BeforeEach
    public void setUp(){
        likeDto = LikeDto.builder().id(10L).userId(CURRENT_USER_ID).build();
        like = Like.builder().id(10L).userId(CURRENT_USER_ID).build();
        userDto = new UserDto(CURRENT_USER_ID, "username", "mail");
        post = Post.builder().id(POST_ID).build();
        comment = Comment.builder().id(COMMENT_ID).build();
    }

    @Test
    void likePostSaveLikeInDBTest(){
        likeDto.setPostId(POST_ID);
        like.setPost(post);
        Mockito.when(userServiceClient.getUser(CURRENT_USER_ID)).thenReturn(userDto);
        Mockito.when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        Mockito.when(likeRepository.save(like)).thenReturn(like);

        assertEquals(likeDto, likeService.likePost(likeDto, CURRENT_USER_ID));
        Mockito.verify(likeRepository, Mockito.times(1))
                .save(like);
    }

    @Test
    void removeLikeFromPostUpdateDBTest(){
        likeDto.setPostId(POST_ID);
        post.setLikes(List.of(like));
        Mockito.when(userServiceClient.getUser(CURRENT_USER_ID)).thenReturn(userDto);
        Mockito.when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        Mockito.when(likeRepository.findById(likeDto.getId())).thenReturn(Optional.of(like));

        assertEquals(likeDto, likeService.removeLikeFromPost(likeDto, CURRENT_USER_ID));
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByPostIdAndUserId(POST_ID, CURRENT_USER_ID);
    }

    @Test
    void likeCommentSaveLikeInDBTest(){
        likeDto.setCommentId(COMMENT_ID);
        like.setComment(comment);
        Mockito.when(userServiceClient.getUser(CURRENT_USER_ID)).thenReturn(userDto);
        Mockito.when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        Mockito.when(likeRepository.save(like)).thenReturn(like);

        assertEquals(likeDto, likeService.likeComment(likeDto, CURRENT_USER_ID));
        Mockito.verify(likeRepository, Mockito.times(1))
                .save(like);
    }

    @Test
    void removeLikeFromCommentUpdateDBTest(){
        likeDto.setCommentId(COMMENT_ID);
        comment.setLikes(List.of(like));
        Mockito.when(userServiceClient.getUser(CURRENT_USER_ID)).thenReturn(userDto);
        Mockito.when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        Mockito.when(likeRepository.findById(likeDto.getId())).thenReturn(Optional.of(like));

        assertEquals(likeDto, likeService.removeLikeFromComment(likeDto, CURRENT_USER_ID));
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByCommentIdAndUserId(COMMENT_ID, CURRENT_USER_ID);
    }
}