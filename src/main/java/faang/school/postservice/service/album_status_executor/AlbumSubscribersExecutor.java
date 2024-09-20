package faang.school.postservice.service.album_status_executor;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlbumSubscribersExecutor implements AlbumStatusExecutor {

    private final UserServiceClient userServiceClient;

    @Override
    public AlbumStatus getStatus() {
        return AlbumStatus.SUBSCRIBERS;
    }

    @Override
    public Album compute(Album album, Long userId) {
        List<Long> followerIds = userServiceClient.getUser(album.getAuthorId())
                .getFollowerIds();

        if (followerIds.contains(userId)) {
            return album;
        } else {
            throw new IllegalArgumentException("This album is available only followers");
        }
    }

    @Override
    public List<Album> filter(List<Album> albums, Long userId) {
        List<Long> albumAuthorIds = albums.stream()
                .map(Album::getAuthorId)
                .distinct()
                .toList();

        Map<Long, List<Long>> subscribersIdsByAuthorId = userServiceClient.getUsersByIds(albumAuthorIds).stream()
                .collect(Collectors.toMap(UserDto::getId, UserDto::getFollowerIds));

        return albums.stream()
                .filter(album -> subscribersIdsByAuthorId.get(album.getAuthorId()).contains(userId))
                .toList();
    }
}
