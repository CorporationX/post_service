package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exсeption.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @InjectMocks
    LikeService likeService;
    @Mock
    LikeDto like;
    @Mock
    LikeRepository likeRepository;
    @Mock
    LikeMapper likeMapper;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserServiceClient userServiceClient;

    private final Post somePost = Post.builder().id(11).likes(likeList).comments(commentList).build();
    private final Like someLike = Like.builder().id(5L).build();
    private final Comment someComment = Comment.builder().post(somePost).id(2L).likes(likeList).build();

    private final static List<Like> likeList = new ArrayList<>();
    private final static List<Comment> commentList = new ArrayList<>();

    @BeforeEach
    public void init(){
        likeList.add(someLike);
        commentList.add(someComment);
    }

    @Test
    void addLikeToPost() {
        when(postRepository.findById(11L)).thenReturn(Optional.ofNullable(somePost));
        when(likeMapper.toEntity(like)).thenReturn(someLike);
        likeService.addLikeToPost(11, like);
        verify(likeRepository, times(1)).save(someLike);
    }

    @Test
    void addLikeToComment() {
        when(commentRepository.findById(11L)).thenReturn(Optional.ofNullable(someComment));
        try {
            likeService.addLikeToComment(11, like);
        } catch (DataValidationException e){
            assertEquals("Cannot like post and comment together !", e.getMessage());
        }
    }

    @Test
    void deleteLikeFromPost() {
        likeService.deleteLikeFromPost(11,11);
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(11,11);
    }

    @Test
    void deleteLikeFromComment() {
        likeService.deleteLikeFromComment(22,22);
        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(22,22);
    }
}