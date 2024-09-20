package faang.school.postservice.service.album_status_executor;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumStatus;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumSomeUsersExecutor implements AlbumStatusExecutor {

    private final AlbumRepository albumRepository;

    @Override
    public AlbumStatus getStatus() {
        return AlbumStatus.SOME_USERS;
    }

    @Override
    public Album compute(Album album, Long userId) {
        List<Long> userIdsWithAccess = albumRepository.findUserIdsWithAlbumAccess(album.getId());
        if (userIdsWithAccess.contains(userId)) {
            return album;
        } else {
            throw new IllegalArgumentException("This album don`t available this user");
        }
    }

    @Override
    public List<Album> filter(List<Album> albums, Long userId) {
        List<Long> albumIdsForUser = albumRepository.findAlbumIdsWithUserAccess(userId);

        return albums.stream()
                .filter(album -> albumIdsForUser.contains(album.getId()))
                .toList();
    }
}
