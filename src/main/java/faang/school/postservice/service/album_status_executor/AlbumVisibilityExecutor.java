package faang.school.postservice.service.album_status_executor;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;

import java.util.List;

public interface AlbumVisibilityExecutor {
    AlbumVisibility getStatus();

    Album compute(Album album, Long userId);

    List<Album> filter(List<Album> albums, Long userId);
}
