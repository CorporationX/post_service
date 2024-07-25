package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/album")
public class AlbumController {

    private final AlbumService albumService;

    @PutMapping("/create")
    public AlbumDto createAlbum(AlbumDto albumDto) {
        return albumService.createAlbum(albumDto);
    }

    @GetMapping("/getAlbum/{albumId}")
    public AlbumDto getAlbum(@PathVariable Long albumId) {
        return albumService.getAlbum(albumId);
    }
}
