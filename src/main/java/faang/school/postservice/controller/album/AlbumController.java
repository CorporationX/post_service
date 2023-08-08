package faang.school.postservice.controller.album;

import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.album.DeleteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
public class AlbumController {
    private final AlbumService service;

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long id) {
        DeleteResult result = service.deleteAlbumOfCertainUser(id);

        if (result == DeleteResult.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album not found.");
        } else if (result == DeleteResult.NOT_AUTHORIZED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to delete this album.");
        } else {
            return ResponseEntity.ok("Album deleted successfully.");
        }
    }
}

