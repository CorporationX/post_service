package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.mapper.like.LikeRequestMapper;
import faang.school.postservice.mapper.like.LikeResponseMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.adapter.CommentRepositoryAdapter;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static faang.school.postservice.LikeTestConstants.COMMENT;
import static faang.school.postservice.LikeTestConstants.COMMENT_ID;
import static faang.school.postservice.LikeTestConstants.LIKE_COMMENT;
import static faang.school.postservice.LikeTestConstants.LIKE_COMMENT_RESPONSE_DTO;
import static faang.school.postservice.LikeTestConstants.LIKE_POST;
import static faang.school.postservice.LikeTestConstants.LIKE_POST_RESPONSE_DTO;
import static faang.school.postservice.LikeTestConstants.LIKE_REQUEST_DTO;
import static faang.school.postservice.LikeTestConstants.POST;
import static faang.school.postservice.LikeTestConstants.POST_ID;
import static faang.school.postservice.LikeTestConstants.USERS_IDS_WHO_LIKED_THE_COMMENT;
import static faang.school.postservice.LikeTestConstants.USERS_IDS_WHO_LIKED_THE_POST;
import static faang.school.postservice.LikeTestConstants.USERS_WHO_LIKED_THE_COMMENT;
import static faang.school.postservice.LikeTestConstants.USERS_WHO_LIKED_THE_POST;
import static faang.school.postservice.LikeTestConstants.USER_DTO;
import static faang.school.postservice.LikeTestConstants.USER_ID;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    @Mock
    private CommentRepositoryAdapter commentRepositoryAdapter;

    @Mock
    private LikeRequestMapper likeRequestMapper;

    @Mock
    private LikeResponseMapper likeResponseMapper;

    @Mock
    private PostValidator postValidator;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(likeService, "likeBatch", 100);
    }

    @Test
    @DisplayName("The test should throw BadRequestException when a like on a post already exists")
    void testLikePostThrowBadRequestException() {
        Mockito.when(postValidator.getUserById(USER_ID)).thenReturn(USER_DTO);
        Mockito.when(postRepositoryAdapter.getByIdWithLikes(POST_ID)).thenReturn(POST);
        Mockito.when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(LIKE_POST));

        Assertions.assertThrows(BadRequestException.class,
                () -> likeService.likePost(POST_ID, LIKE_REQUEST_DTO));

        Mockito.verify(postValidator, Mockito.times(1)).getUserById(USER_ID);
        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(POST_ID);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a post does not exist")
    void testLikePostSuccessful() {
        Mockito.when(postValidator.getUserById(USER_ID)).thenReturn(USER_DTO);
        Mockito.when(postRepositoryAdapter.getByIdWithLikes(POST_ID)).thenReturn(POST);
        Mockito.when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        Mockito.when(likeRequestMapper.toEntity(LIKE_REQUEST_DTO)).thenReturn(LIKE_POST);
        Mockito.when(likeRepository.save(LIKE_POST)).thenReturn(LIKE_POST);
        Mockito.when(likeResponseMapper.toDto(LIKE_POST)).thenReturn(LIKE_POST_RESPONSE_DTO);

        LikeResponseDto likeResponseDto = likeService.likePost(POST_ID, LIKE_REQUEST_DTO);

        Assertions.assertEquals(LIKE_POST_RESPONSE_DTO, likeResponseDto);

        Mockito.verify(postValidator, Mockito.times(1)).getUserById(USER_ID);
        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(POST_ID);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(POST_ID, USER_ID);
        Mockito.verify(likeRequestMapper, Mockito.times(1)).toEntity(LIKE_REQUEST_DTO);
        Mockito.verify(likeRepository, Mockito.times(1)).save(LIKE_POST);
        Mockito.verify(likeResponseMapper, Mockito.times(1)).toDto(LIKE_POST);
    }

    @Test
    @DisplayName("The test should throw BadRequestException when a like on a comment already exists")
    void testLikeCommentThrowBadRequestException() {
        Mockito.when(postValidator.getUserById(USER_ID)).thenReturn(USER_DTO);
        Mockito.when(commentRepositoryAdapter.getById(COMMENT_ID)).thenReturn(COMMENT);
        Mockito.when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID))
                .thenReturn(Optional.of(LIKE_COMMENT));

        Assertions.assertThrows(BadRequestException.class,
                () -> likeService.likeComment(COMMENT_ID, LIKE_REQUEST_DTO));

        Mockito.verify(postValidator, Mockito.times(1)).getUserById(USER_ID);
        Mockito.verify(commentRepositoryAdapter, Mockito.times(1)).getById(COMMENT_ID);
        Mockito.verify(likeRepository, Mockito.times(1))
                .findByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a comment does not exist")
    void testLikeCommentSuccessful() {
        Mockito.when(postValidator.getUserById(USER_ID)).thenReturn(USER_DTO);
        Mockito.when(commentRepositoryAdapter.getById(COMMENT_ID)).thenReturn(COMMENT);
        Mockito.when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        Mockito.when(likeRequestMapper.toEntity(LIKE_REQUEST_DTO)).thenReturn(LIKE_COMMENT);
        Mockito.when(likeRepository.save(LIKE_COMMENT)).thenReturn(LIKE_COMMENT);
        Mockito.when(likeResponseMapper.toDto(LIKE_COMMENT)).thenReturn(LIKE_COMMENT_RESPONSE_DTO);

        LikeResponseDto likeResponseDto = likeService.likeComment(COMMENT_ID, LIKE_REQUEST_DTO);

        Assertions.assertEquals(LIKE_COMMENT_RESPONSE_DTO, likeResponseDto);

        Mockito.verify(postValidator, Mockito.times(1)).getUserById(USER_ID);
        Mockito.verify(commentRepositoryAdapter, Mockito.times(1)).getById(COMMENT_ID);
        Mockito.verify(likeRepository, Mockito.times(1))
                .findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        Mockito.verify(likeRequestMapper, Mockito.times(1)).toEntity(LIKE_REQUEST_DTO);
        Mockito.verify(likeRepository, Mockito.times(1)).save(LIKE_COMMENT);
        Mockito.verify(likeResponseMapper, Mockito.times(1)).toDto(LIKE_COMMENT);
    }

    @Test
    @DisplayName("The test should throw BadRequestException when a like on a post does not exist")
    void testRemoveLikeFromPostThrowBadRequestException() {
        Mockito.when(postValidator.getUserById(USER_ID)).thenReturn(USER_DTO);
        Mockito.when(postRepositoryAdapter.getByIdWithLikes(POST_ID)).thenReturn(POST);
        Mockito.when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class,
                () -> likeService.removeLikeFromPost(POST_ID, LIKE_REQUEST_DTO));

        Mockito.verify(postValidator, Mockito.times(1)).getUserById(USER_ID);
        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(POST_ID);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a post exists")
    void testRemoveLikeFromPostSuccessful() {
        Mockito.when(postValidator.getUserById(USER_ID)).thenReturn(USER_DTO);
        Mockito.when(postRepositoryAdapter.getByIdWithLikes(POST_ID)).thenReturn(POST);
        Mockito.when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(LIKE_POST));
        Mockito.doNothing().when(likeRepository).deleteByPostIdAndUserId(POST_ID, USER_ID);
        Mockito.when(likeResponseMapper.toDto(LIKE_POST)).thenReturn(LIKE_POST_RESPONSE_DTO);

        LikeResponseDto likeResponseDto = likeService.removeLikeFromPost(POST_ID, LIKE_REQUEST_DTO);

        Assertions.assertEquals(LIKE_POST_RESPONSE_DTO, likeResponseDto);

        Mockito.verify(postValidator, Mockito.times(1)).getUserById(USER_ID);
        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(POST_ID);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostIdAndUserId(POST_ID, USER_ID);
        Mockito.verify(likeRepository, Mockito.times(1)).deleteByPostIdAndUserId(POST_ID, USER_ID);
        Mockito.verify(likeResponseMapper, Mockito.times(1)).toDto(LIKE_POST);
    }

    @Test
    @DisplayName("The test should throw BadRequestException when a like on a comment does not exist")
    void testRemoveLikeFromCommentThrowBadRequestException() {
        Mockito.when(postValidator.getUserById(USER_ID)).thenReturn(USER_DTO);
        Mockito.when(commentRepositoryAdapter.getById(COMMENT_ID)).thenReturn(COMMENT);
        Mockito.when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class,
                () -> likeService.removeLikeFromComment(COMMENT_ID, LIKE_REQUEST_DTO));

        Mockito.verify(postValidator, Mockito.times(1)).getUserById(USER_ID);
        Mockito.verify(commentRepositoryAdapter, Mockito.times(1)).getById(COMMENT_ID);
        Mockito.verify(likeRepository, Mockito.times(1))
                .findByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a comment exists")
    void testRemoveLikeFromCommentSuccessful() {
        Mockito.when(postValidator.getUserById(USER_ID)).thenReturn(USER_DTO);
        Mockito.when(commentRepositoryAdapter.getById(COMMENT_ID)).thenReturn(COMMENT);
        Mockito.when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID))
                .thenReturn(Optional.of(LIKE_COMMENT));
        Mockito.doNothing().when(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
        Mockito.when(likeResponseMapper.toDto(LIKE_COMMENT)).thenReturn(LIKE_COMMENT_RESPONSE_DTO);

        LikeResponseDto likeResponseDto = likeService.removeLikeFromComment(COMMENT_ID, LIKE_REQUEST_DTO);

        Assertions.assertEquals(LIKE_COMMENT_RESPONSE_DTO, likeResponseDto);

        Mockito.verify(postValidator, Mockito.times(1)).getUserById(USER_ID);
        Mockito.verify(commentRepositoryAdapter, Mockito.times(1)).getById(COMMENT_ID);
        Mockito.verify(likeRepository, Mockito.times(1))
                .findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
        Mockito.verify(likeResponseMapper, Mockito.times(1)).toDto(LIKE_COMMENT);
    }

    @Test
    @DisplayName("The test should return a list of UserDto's when a post exists")
    void testGetUsersWhoLikedPostSuccessful() {
        Mockito.when(postRepositoryAdapter.getById(POST_ID)).thenReturn(POST);
        Mockito.when(likeRepository.findUserIdsByPostId(POST_ID)).thenReturn(USERS_IDS_WHO_LIKED_THE_POST);
        Mockito.when(userServiceClient.getUsersByIds(USERS_IDS_WHO_LIKED_THE_POST))
                .thenReturn(USERS_WHO_LIKED_THE_POST);

        Assertions.assertEquals(USERS_WHO_LIKED_THE_POST, likeService.getUsersWhoLikedPost(POST_ID));

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getById(POST_ID);
        Mockito.verify(likeRepository, Mockito.times(1)).findUserIdsByPostId(POST_ID);
        Mockito.verify(userServiceClient, Mockito.times(1))
                .getUsersByIds(USERS_IDS_WHO_LIKED_THE_POST);
    }

    @Test
    @DisplayName("The test should return a list of UserDto's when a comment exists")
    void testGetUsersWhoLikedCommentSuccessful() {
        Mockito.when(commentRepositoryAdapter.getById(COMMENT_ID)).thenReturn(COMMENT);
        Mockito.when(likeRepository.findUserIdsByCommentId(COMMENT_ID)).thenReturn(USERS_IDS_WHO_LIKED_THE_COMMENT);
        Mockito.when(userServiceClient.getUsersByIds(USERS_IDS_WHO_LIKED_THE_COMMENT))
                .thenReturn(USERS_WHO_LIKED_THE_COMMENT);

        Assertions.assertEquals(USERS_WHO_LIKED_THE_COMMENT, likeService.getUsersWhoLikedComment(COMMENT_ID));

        Mockito.verify(commentRepositoryAdapter, Mockito.times(1)).getById(COMMENT_ID);
        Mockito.verify(likeRepository, Mockito.times(1)).findUserIdsByCommentId(COMMENT_ID);
        Mockito.verify(userServiceClient, Mockito.times(1))
                .getUsersByIds(USERS_IDS_WHO_LIKED_THE_COMMENT);
    }
}
