package faang.school.postservice.api;

import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

public interface MediaApi<ID, MediaInfo, Media> {
    MediaInfo save(Media media);

    List<MediaInfo> saveAll(Iterable<Media> medias);

    Optional<MediaInfo> update(ID id, Media media);

    Optional<List<MediaInfo>> updateAll(Iterable<Pair<ID, Media>> medias);

    void delete(ID id);

    void deleteAll(Iterable<ID> ids);
}
