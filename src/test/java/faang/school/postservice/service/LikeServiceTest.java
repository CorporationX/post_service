package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.LikeServiceValidator;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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
    LikeServiceValidator likeServiceValidator;
    @Mock
    LikeMapper likeMapper;

    private final LikeDto likeDto = LikeDto.builder().userId(1L).build();
    private final LikeDto returnedDto = LikeDto.builder().userId(1L).id(5L).build();
    private final Post somePost = Post.builder().id(11).likes(likeList).comments(commentList).build();
    private final Like someLike = Like.builder().id(5L).userId(1L).build();
    private final Comment someComment = Comment.builder().post(somePost).id(2L).likes(likeList).build();

    private final static List<Like> likeList = new ArrayList<>();
    private final static List<Comment> commentList = new ArrayList<>();

    @BeforeEach
    public void init(){
        likeList.add(someLike);
        commentList.add(someComment);
    }

    @Test
    void userNotExistOnPost(){
        try{
            likeService.addLikeToPost(1,like);
        } catch (FeignException e){
            assertEquals("User with this Id does not exist !", e.getMessage());
        }
    }

    @Test
    void userNotExistOnComment(){
        try{
            likeService.addLikeToComment(1,like);
        } catch (FeignException e){
            assertEquals("User with this Id does not exist !", e.getMessage());
        }
    }

    @Test
    void likeOnPostAlreadyExist(){
        try {
            likeService.addLikeToPost(11, likeDto);
        } catch (DataValidationException e){
            assertEquals("Like on post already exist !", e.getMessage());
        }
    }

    @Test
    void likeOnCommentAlreadyExist(){
        try {
            likeService.addLikeToComment(11, likeDto);
        } catch (DataValidationException e){
            assertEquals("Like on comment already exist !", e.getMessage());
        }
    }

    @Test
    void addLikeToPostAndCommentTogether() {
        try {
            likeService.addLikeToPost(11, like);
        } catch (DataValidationException e){
            assertEquals("Cannot like post and comment together !", e.getMessage());
        }
    }

    @Test
    void addLikeToCommentAndPostTogether() {
        try {
            likeService.addLikeToComment(11, like);
        } catch (DataValidationException e){
            assertEquals("Cannot like post and comment together !", e.getMessage());
        }
    }

    @Test
    void addLikeToPost() {
        when(likeMapper.toEntity(like)).thenReturn(someLike);
        likeService.addLikeToPost(11, like);
        verify(likeRepository, times(1)).save(someLike);
    }

    @Test
    void addLikeToComment() {
        when(likeMapper.toEntity(like)).thenReturn(someLike);
        likeService.addLikeToComment(11, like);
        verify(likeRepository, times(1)).save(someLike);
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

    @Test
    void checkReturnedDto(){
        when(likeMapper.toDto(likeRepository.save(someLike))).thenReturn(returnedDto);
        LikeDto returned = likeService.addLikeToPost(13, likeDto);
        assertEquals(returned.getId(), someLike.getId());
    }
}