package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.album.AlbumDataValidationException;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
public class AlbumController {
    private final AlbumService service;

    @PostMapping()
    public AlbumDto createAlbum(@RequestBody AlbumDto albumDto) {
        validateAlbumController(albumDto);
        return service.createAlbum(albumDto);
    }

    private void validateAlbumController(AlbumDto albumDto) {
        if (albumDto.getTitle().isEmpty() || albumDto.getDescription().isEmpty()) {
            throw new AlbumDataValidationException("Incorrect input data");
        }
    }
}
