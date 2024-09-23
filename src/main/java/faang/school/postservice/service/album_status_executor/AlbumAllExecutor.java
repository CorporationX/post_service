package faang.school.postservice.service.album_status_executor;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlbumAllExecutor implements AlbumVisibilityExecutor {

    @Override
    public AlbumVisibility getStatus() {
        return AlbumVisibility.ALL;
    }

    @Override
    public Album compute(Album album, Long userId) {
        return album;
    }

    @Override
    public List<Album> filter(List<Album> albums, Long userId) {
        return albums;
    }
}
