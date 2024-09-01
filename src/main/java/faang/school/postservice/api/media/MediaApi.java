package faang.school.postservice.api.media;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface MediaApi<ID, MediaInfo, Media> {

    List<MediaInfo> save(Iterable<Media> medias);

    // It's a pity that Java doesn't have aliases like Kotlin...
    List<MediaInfo> update(Map<ID, Media> medias);

    void delete(Set<ID> ids);

    Map<ID, InputStream> getInputStreams(Set<ID> ids);
}
