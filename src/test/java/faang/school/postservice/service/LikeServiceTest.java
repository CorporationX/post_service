package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeRepository likeRepository;
    private static final long POST_ID_ONE = 1L;
    private static final long COMMENT_ID_ONE = 1L;
    private static final long TEST_LIKES_VALUE = 356L;
    private List<Long> userIds;
    private List<UserDto> usersLiked;
    private List<Like> likes;

    @Nested
    class SingleRequestTests {
        @BeforeEach
        void setup() {
            usersLiked = List.of(UserDto.builder().build(), UserDto.builder().build());
            userIds = List.of(1L, 2L);
        }

        @Test
        @DisplayName("When post id passed then return all users from user service who like this post")
        public void whenPostIdPassedThenReturnUserDtoWhoLikedPostList() {
            when(likeService.getAllUsersByPostId(POST_ID_ONE)).thenReturn(usersLiked);
            List<UserDto> userDtos = likeService.getAllUsersByPostId(POST_ID_ONE);
            assertEquals(usersLiked.size(), userIds.size());
            assertEquals(usersLiked, userDtos);
        }

        @Test
        @DisplayName("When comment id passed then return all users from user service who like this comment")
        public void whenCommentIdPassedThenReturnUserDtoWhoLikedCommentList() {
            when(likeService.getAllUsersByCommentId(COMMENT_ID_ONE)).thenReturn(usersLiked);
            List<UserDto> userDtos = likeService.getAllUsersByCommentId(COMMENT_ID_ONE);
            assertEquals(usersLiked.size(), userIds.size());
            assertEquals(usersLiked, userDtos);
        }
    }

    @Nested
    class BatchRequestsTests {
        @BeforeEach
        void setup() {
            usersLiked = new ArrayList<>();
            userIds = new ArrayList<>();
            likes = new ArrayList<>();
            for (int i = 0; i < TEST_LIKES_VALUE; i++) {
                usersLiked.add(UserDto.builder().id(i + 1L).build());
            }
            for (int i = 0; i < TEST_LIKES_VALUE; i++) {
                userIds.add(i + 1L);
            }
            for (int i = 0; i < TEST_LIKES_VALUE; i++) {
                likes.add(Like.builder().id(i + 1).build());
            }
        }

        @Test
        @DisplayName("When more than 100 people liked comment " +
                "than it goes by batches to user service and returns back")
        public void whenCommentIdPassedAndListIsMoreThanOneHundredThenBatchesMade() {
            when(likeRepository.findByCommentId(COMMENT_ID_ONE)).thenReturn(likes);

            when(likeService.getAllUsersByCommentId(COMMENT_ID_ONE)).thenReturn(usersLiked);

            List<UserDto> userDtos = likeService.getAllUsersByCommentId(COMMENT_ID_ONE);
            assertEquals(likes.size(), usersLiked.size());
            assertEquals(usersLiked, userDtos);
        }

        @Test
        @DisplayName("When more than 100 people liked post " +
                "than it goes by batches to user service and returns back")
        public void whenPostIdPassedAndListIsMoreThanOneHundredThenBatchesMade() {
            when(likeRepository.findByPostId(POST_ID_ONE)).thenReturn(likes);

            when(likeService.getAllUsersByPostId(POST_ID_ONE)).thenReturn(usersLiked);

            List<UserDto> userDtos = likeService.getAllUsersByPostId(POST_ID_ONE);
            assertEquals(likes.size(), usersLiked.size());
            assertEquals(usersLiked, userDtos);
        }
    }
}
