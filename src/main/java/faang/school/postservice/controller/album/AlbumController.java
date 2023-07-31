package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.validator.album.AlbumValidator;
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
    private final AlbumValidator albumValidator;

    @PostMapping()
    public AlbumDto createAlbum(@RequestBody AlbumDto albumDto) {
        albumValidator.validateAlbumController(albumDto);
        return service.createAlbum(albumDto);
    }
}
