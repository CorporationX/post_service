package faang.school.postservice.controller.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.dane.DANEException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/albums")
public class AlbumController {
    private final AlbumService service;

    @PostMapping()
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody AlbumDto dto) throws JsonProcessingException {
        return ResponseEntity.ok().body(service.createAlbum(dto));
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbum(@RequestHeader(value = "x-user-id") long userId, @PathVariable long albumId) throws JsonProcessingException {
        var dto = service.getAlbum(albumId, userId);
        return ResponseEntity.ok().body(dto);
    }

    @PutMapping
    public ResponseEntity<AlbumDto> updateAlbum(@RequestHeader(value = "x-user-id") long userId, @RequestBody AlbumDto dto) throws JsonProcessingException {
        if (dto.getId() == null) throw new DataValidationException("U cannot update an album without id");
        var responseDto = service.update(dto, userId);
        return ResponseEntity.ok().body(responseDto);
    }
}
