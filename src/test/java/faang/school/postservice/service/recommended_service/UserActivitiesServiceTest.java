package faang.school.postservice.service.recommended_service;

import faang.school.postservice.dto.recommended_service.UserActivitiesGetDto;
import faang.school.postservice.dto.recommended_service.UserActivitiesViewDto;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Тестирование сервиса {@link UserActivitiesService}.
 */
@ExtendWith(MockitoExtension.class)
class UserActivitiesServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private UserActivitiesService userActivitiesService;

    @Test
    void getUserActivitiesShouldCollectAllActivitiesForEveryUser() {
        Long firstUserId = 1L;
        Long secondUserId = 2L;
        int limit = 3;
        when(postRepository.findContentByAuthorId(firstUserId, limit)).thenReturn(List.of("own post"));
        when(postRepository.findContentByAuthorId(secondUserId, limit)).thenReturn(List.of("another post"));
        when(likeRepository.findPostIdsByUserId(firstUserId)).thenReturn(List.of(10L, 10L));
        when(likeRepository.findPostIdsByUserId(secondUserId)).thenReturn(List.of());
        when(commentRepository.findPostIdByAuthorId(firstUserId)).thenReturn(List.of(20L, 20L));
        when(commentRepository.findPostIdByAuthorId(secondUserId)).thenReturn(List.of(21L));
        when(commentRepository.findContentByAuthorId(firstUserId, limit)).thenReturn(List.of("own comment"));
        when(commentRepository.findContentByAuthorId(secondUserId, limit)).thenReturn(List.of());
        when(postRepository.findContentByIds(List.of(10L), limit)).thenReturn(List.of("liked post"));
        when(postRepository.findContentByIds(List.of(20L), limit)).thenReturn(List.of("commented post"));
        when(postRepository.findContentByIds(List.of(21L), limit)).thenReturn(List.of("second commented post"));

        UserActivitiesViewDto result = userActivitiesService.getUserActivities(
                new UserActivitiesGetDto(List.of(firstUserId, secondUserId), limit));

        Map<String, List<String>> firstUserActivities = result.activities().get(firstUserId);
        assertEquals(List.of("own post"), firstUserActivities.get("ownPosts"));
        assertEquals(List.of("liked post"), firstUserActivities.get("likedPosts"));
        assertEquals(List.of("liked post"), firstUserActivities.get("postsByLikedComments"));
        assertEquals(List.of("commented post"), firstUserActivities.get("commentedPosts"));
        assertEquals(List.of("own comment"), firstUserActivities.get("ownComments"));
        assertEquals(List.of(), result.activities().get(secondUserId).get("likedPosts"));
        assertEquals(List.of("second commented post"), result.activities().get(secondUserId).get("commentedPosts"));
        verify(likeRepository, times(2)).findPostIdsByUserId(firstUserId);
        verify(commentRepository).findContentByAuthorId(secondUserId, limit);
    }
}
