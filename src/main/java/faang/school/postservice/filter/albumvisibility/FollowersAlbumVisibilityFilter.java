package faang.school.postservice.filter.albumvisibility;

import faang.school.postservice.client.SubscriptionClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.postservice.model.AlbumVisibility.FOLLOWERS;

@Component
@RequiredArgsConstructor
public class FollowersAlbumVisibilityFilter implements AlbumVisibilityFilter {

    private final AlbumMapper albumMapper;
    private final UserContext userContext;
    private final SubscriptionClient subscriptionClient;

    @Override
    public AlbumResponseDto apply(Album album) {
        long userId = userContext.getUserId();
        List<UserDto> followers = subscriptionClient.getFollowersByUserId(album.getAuthorId());
        boolean isUserFollower = followers.stream()
                .map(UserDto::id)
                .anyMatch(followerId -> followerId != null && followerId == userId);
        if (isUserFollower) {
            return albumMapper.toDto(album);
        }
        throw new AlbumAccessDeniedException(
                String.format("Access denied for user with id = %d for album with id = %d. "
                        + "User isn't follower for author with id = %d", userId, album.getId(), album.getAuthorId()));
    }

    @Override
    public AlbumVisibility getAlbumVisibility() {
        return FOLLOWERS;
    }
}