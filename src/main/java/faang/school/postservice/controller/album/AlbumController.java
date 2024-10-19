package faang.school.postservice.controller.album;

import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Заглушка
 */
@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<String> createAlbum() {
        albumService.createAlbum();
        return ResponseEntity.ok().body("""
                {
                    "message": "success"
                }""");
    }
}
