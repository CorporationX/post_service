package faang.school.postservice.service.impl.album;

import faang.school.postservice.model.event.AlbumCreatedEvent;
import faang.school.postservice.publisher.AlbumCreatedEventPublisher;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Заглушка
 */
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumCreatedEventPublisher albumCreatedEventPublisher;


    @Override
    public void createAlbum() {
        var albumCreatedEvent = AlbumCreatedEvent.builder()
                .userId(1)
                .albumId(2)
                .albumName("Test Album name")
                .build();

        albumCreatedEventPublisher.publish(albumCreatedEvent);
    }
}
