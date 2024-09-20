package faang.school.postservice.service.album_status_executor;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumOnlyAuthorExecutor implements AlbumStatusExecutor {

    @Override
    public AlbumStatus getStatus() {
        return AlbumStatus.ONLY_AUTHOR;
    }

    @Override
    public Album compute(Album album, Long userId) {
        if (userId == album.getAuthorId()) {
            return album;
        } else {
            throw new IllegalArgumentException("This album is available only author");
        }
    }

    @Override
    public List<Album> filter(List<Album> albums, Long userId) {
        return albums.stream()
                .filter(album -> userId == album.getAuthorId())
                .toList();
    }
}
