package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.like.LikeOperatingException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userServiceClient;

    private List<Like> likes;
    private Post likedPost;
    private Comment likedComment;
    private List<UserDto> usersLikedObjectList;

    @BeforeEach
    void setUp() {
        likes = List.of(Like.builder().userId(1L).build());

        likedPost = Post.builder()
                .content("Content")
                .published(true)
                .deleted(false)
                .likes(likes)
                .build();

        likedComment = Comment.builder()
                .content("Content")
                .authorId(1)
                .post(likedPost)
                .likes(likes)
                .build();

        usersLikedObjectList = List.of(UserDto.builder().id(1L).build());
    }

    @Nested
    class PositiveTests {
        @BeforeEach
        void setUp() {
            when(userServiceClient.getUsersByIds(anyList())).thenReturn(usersLikedObjectList);
        }

        @DisplayName("Should return a list of users who liked post with passed id when such post exists")
        @Test
        void getUsersLikedPostByPostId() {
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(likedPost));

            List<UserDto> actualResult = likeService.getUsersLikedPostByPostId(anyLong());

            assertEquals(usersLikedObjectList, actualResult);
        }

        @DisplayName("Should return a list of users who liked comment with passed id  when such comment exists")
        @Test
        void getUsersLikedCommentByCommentId() {
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(likedComment));

            List<UserDto> actualResult = likeService.getUsersLikedCommentByCommentId(anyLong());

            assertEquals(usersLikedObjectList, actualResult);
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("Should throw exception when there is no post for passed id")
        @Test
        void getUsersLikedPostByPostId() {
            doThrow(LikeOperatingException.class).when(postRepository).findById(anyLong());

            assertThrows(LikeOperatingException.class, () -> likeService.getUsersLikedPostByPostId(anyLong()));

            verifyNoInteractions(userServiceClient);
        }

        @DisplayName("Should throw exception when there is no comment for passed id")
        @Test
        void getUsersLikedCommentByCommentId() {
            doThrow(LikeOperatingException.class).when(commentRepository).findById(anyLong());

            assertThrows(LikeOperatingException.class, () -> likeService.getUsersLikedCommentByCommentId(anyLong()));

            verifyNoInteractions(userServiceClient);
        }
    }
}