package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/albums")
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public AlbumDto createAlbum(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        return albumService.createAlbum(albumCreateDto);
    }
}
