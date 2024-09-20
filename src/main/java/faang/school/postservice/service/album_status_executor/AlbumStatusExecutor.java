package faang.school.postservice.service.album_status_executor;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumStatus;

import java.util.List;

public interface AlbumStatusExecutor {
    AlbumStatus getStatus();

    Album compute(Album album, Long userId);

    List<Album> filter(List<Album> albums, Long userId);
}
