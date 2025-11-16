package faang.school.postservice.validate;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public boolean hasPermission(Album album) {
        if (userContext.getUserId() == (album.getAuthorId())) {
            return true;
        }
        if (album.getVisibility().equals(AlbumVisibility.SELECTED_USERS)) {
            return album.getFavouriteUserIds().contains(userContext.getUserId());
        }
        if (album.getVisibility().equals(AlbumVisibility.SUBSCRIBERS)) {
            return userServiceClient.getFollowers(album.getAuthorId()).stream()
                    .anyMatch(subscriptionUserDto -> subscriptionUserDto.id().equals(userContext.getUserId()));
        }

        return album.getVisibility().equals(AlbumVisibility.PUBLIC);
    }
}
