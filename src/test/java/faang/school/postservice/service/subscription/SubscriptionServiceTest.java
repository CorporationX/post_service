package faang.school.postservice.service.subscription;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Post testPost;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        ReflectionTestUtils.setField(subscriptionService, "postsAgeMonths", 6);
        testPost = Post.builder()
                .id(1L)
                .authorId(1L)
                .projectId(1L)
                .published(true)
                .createdAt(now)
                .build();
    }

    @Test
    void getAuthorIds_WithValidPosts_ReturnsAuthorIds() {
        Post post1 = Post.builder().authorId(1L).published(true).createdAt(now).build();
        Post post2 = Post.builder().authorId(2L).published(true).createdAt(now).build();
        UserDto user1 = UserDto.builder().id(1L).username("User1").build();
        UserDto user2 = UserDto.builder().id(2L).username("User2").build();
        when(postRepository.findByAuthorIdIsNotNullAndCreatedAtAfterAndPublishedTrue(any()))
                .thenReturn(Arrays.asList(post1, post2));
        when(userServiceClient.findById(1L)).thenReturn(Optional.of(user1));
        when(userServiceClient.findById(2L)).thenReturn(Optional.of(user2));
        Set<Long> result = subscriptionService.getAuthorIds();
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        verify(postRepository).findByAuthorIdIsNotNullAndCreatedAtAfterAndPublishedTrue(any());
        verify(userServiceClient, times(2)).findById(any());
    }

    @Test
    void getAuthorIds_WithNonExistentAuthor_FiltersOutInvalidAuthor() {
        Post post1 = Post.builder().authorId(1L).published(true).createdAt(now).build();
        Post post2 = Post.builder().authorId(2L).published(true).createdAt(now).build();
        UserDto user1 = UserDto.builder().id(1L).username("User1").build();
        when(postRepository.findByAuthorIdIsNotNullAndCreatedAtAfterAndPublishedTrue(any()))
                .thenReturn(Arrays.asList(post1, post2));
        when(userServiceClient.findById(1L)).thenReturn(Optional.of(user1));
        when(userServiceClient.findById(2L)).thenReturn(Optional.empty());
        Set<Long> result = subscriptionService.getAuthorIds();
        assertEquals(1, result.size());
        assertTrue(result.contains(1L));
        assertFalse(result.contains(2L));
    }

    @Test
    void getAuthorIds_WithRepositoryException_ThrowsException() {
        when(postRepository.findByAuthorIdIsNotNullAndCreatedAtAfterAndPublishedTrue(any()))
                .thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> subscriptionService.getAuthorIds());
    }

    @Test
    void getProjectIds_WithValidPosts_ReturnsProjectIds() {
        Post post1 = Post.builder().projectId(1L).published(true).createdAt(now).build();
        Post post2 = Post.builder().projectId(2L).published(true).createdAt(now).build();
        when(postRepository.findByProjectIdIsNotNullAndCreatedAtAfterAndPublishedTrue(any()))
                .thenReturn(Arrays.asList(post1, post2));
        Set<Long> result = subscriptionService.getProjectIds();
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        verify(postRepository).findByProjectIdIsNotNullAndCreatedAtAfterAndPublishedTrue(any());
    }

    @Test
    void getProjectIds_WithRepositoryException_ThrowsException() {
        when(postRepository.findByProjectIdIsNotNullAndCreatedAtAfterAndPublishedTrue(any()))
                .thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> subscriptionService.getProjectIds());
    }

    @Test
    void getSubscriberIds_WithAuthorAndProject_ReturnsCombinedSubscribers() {
        List<Long> authorSubscribers = Arrays.asList(1L, 2L);
        List<Long> projectSubscribers = Arrays.asList(2L, 3L);
        when(userServiceClient.getUserSubscribersIds(testPost.getAuthorId()))
                .thenReturn(authorSubscribers);
        when(userServiceClient.getProjectSubscriptions(testPost.getProjectId()))
                .thenReturn(projectSubscribers);
        Set<Long> result = subscriptionService.getSubscriberIds(testPost);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(Arrays.asList(1L, 2L, 3L)));
    }

    @Test
    void getSubscriberIds_WithOnlyAuthor_ReturnsAuthorSubscribers() {
        testPost.setProjectId(null);
        List<Long> authorSubscribers = Arrays.asList(1L, 2L);
        when(userServiceClient.getUserSubscribersIds(testPost.getAuthorId()))
                .thenReturn(authorSubscribers);
        Set<Long> result = subscriptionService.getSubscriberIds(testPost);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(1L, 2L)));
    }

    @Test
    void getSubscriberIds_WithOnlyProject_ReturnsProjectSubscribers() {
        testPost.setAuthorId(null);
        List<Long> projectSubscribers = Arrays.asList(1L, 2L);
        when(userServiceClient.getProjectSubscriptions(testPost.getProjectId()))
                .thenReturn(projectSubscribers);
        Set<Long> result = subscriptionService.getSubscriberIds(testPost);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(1L, 2L)));
    }

    @Test
    void getSubscriberIds_WithClientException_ReturnsEmptySet() {
        when(userServiceClient.getUserSubscribersIds(anyLong()))
                .thenThrow(new RuntimeException("Service unavailable"));
        Set<Long> result = subscriptionService.getSubscriberIds(testPost);
        assertEquals(Collections.emptySet(), result);
    }
}