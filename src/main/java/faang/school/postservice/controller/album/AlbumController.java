package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/album")
public class AlbumController {
    private final AlbumService service;

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable long albumId, @Validated @RequestBody AlbumDto updatedAlbum) {
        service.updateAlbum(albumId, updatedAlbum);
        return ResponseEntity.accepted().body(updatedAlbum);
    }
}
