package faang.school.postservice.service.userModeration;

import faang.school.postservice.model.Post;
import faang.school.postservice.redis.UserBanPublisher;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserModerationServiceTest {
    @InjectMocks
    private UserModerationService userModerationService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserBanPublisher userBanPublisher;

    //Positive

    @Test
    void test_checkAndBanUsersWithUnverifiedPosts() {
        List<Post> posts = generatePosts(6, 1L);
        when(postRepository.findByVerifiedFalse()).thenReturn(posts);

        userModerationService.checkAndBanUsersWithUnverifiedPosts();

        verify(userBanPublisher).publishUserBan(1L);
    }

    @Test
    void test_checkAndNotBanUsersWithFiveOrFewerUnverifiedPosts() {
        List<Post> posts = generatePosts(5, 2L);
        when(postRepository.findByVerifiedFalse()).thenReturn(posts);

        userModerationService.checkAndBanUsersWithUnverifiedPosts();

        verify(userBanPublisher, never()).publishUserBan(anyLong());
    }

    @Test
    void test_checkAndBanOnlyUsersWithMoreFiveUnverifiedPosts() {
        List<Post> posts = Stream.of(
                generatePosts(6, 1L),
                generatePosts(4, 2L)
        ).flatMap(List::stream).toList();
        when(postRepository.findByVerifiedFalse()).thenReturn(posts);

        userModerationService.checkAndBanUsersWithUnverifiedPosts();

        verify(userBanPublisher).publishUserBan(1L);
        verify(userBanPublisher, never()).publishUserBan(2L);
    }

    private List<Post> generatePosts(int count, Long authorId) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Post post = new Post();
                    post.setAuthorId(authorId);
                    post.setVerified(false);
                    return post;
                })
                .toList();
    }
}