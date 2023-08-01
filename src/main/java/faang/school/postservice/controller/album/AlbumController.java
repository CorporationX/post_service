package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService service;


    public AlbumDto addPostToAlbum(long userId, long albumId, long postId) {
        return service.addPostToAlbum(userId, albumId, postId);
    }
}
