package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.adapter.CommentRepositoryAdapter;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
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

import static faang.school.postservice.LikeTestConstants.COMMENT;
import static faang.school.postservice.LikeTestConstants.COMMENT_ID;
import static faang.school.postservice.LikeTestConstants.POST;
import static faang.school.postservice.LikeTestConstants.POST_ID;
import static faang.school.postservice.LikeTestConstants.USERS_IDS_WHO_LIKED_THE_COMMENT;
import static faang.school.postservice.LikeTestConstants.USERS_IDS_WHO_LIKED_THE_POST;
import static faang.school.postservice.LikeTestConstants.USERS_WHO_LIKED_THE_COMMENT;
import static faang.school.postservice.LikeTestConstants.USERS_WHO_LIKED_THE_POST;

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

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(likeService, "likeBatch", 100);
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
