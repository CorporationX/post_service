package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.UserBannerPublisher;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBannerServiceTest {
    @Mock
    PostRepository postRepository;
    @Mock
    UserBannerPublisher userBannerPublisher;
    @InjectMocks
    UserBannerService userBannerService;
    Post firstPost;
    Post secondPost;
    Post thirdPost;
    Post forthPost;
    Post fifthPost;
    Post sixthPost;

    List<Post> unverifiedPosts = new ArrayList<>();
    List<Long> userIdsToBan = new ArrayList<>();
    Long authorUserId;

    @BeforeEach
    void setUp() {
        authorUserId = 1L;

        firstPost = Post.builder()
                .authorId(authorUserId)
                .verified(false)
                .build();
        secondPost = Post.builder()
                .authorId(authorUserId)
                .verified(false)
                .build();
        thirdPost = Post.builder()
                .authorId(authorUserId)
                .verified(false)
                .build();
        forthPost = Post.builder()
                .authorId(authorUserId)
                .verified(false)
                .build();
        fifthPost = Post.builder()
                .authorId(authorUserId)
                .verified(false)
                .build();
        sixthPost = Post.builder()
                .authorId(authorUserId)
                .verified(false)
                .build();

        unverifiedPosts.addAll(List.of(firstPost, secondPost, thirdPost, forthPost, fifthPost, sixthPost));
        userIdsToBan.add(authorUserId);


    }

    @Test
    void testBanPosts() {
        when(postRepository.findAllByVerified(false)).thenReturn(unverifiedPosts);

        userBannerService.banPosts();

        verify(userBannerPublisher, times(1)).publish(userIdsToBan);
        verify(postRepository, times(1)).deleteAllByAuthorIdIn(userIdsToBan);
    }
}