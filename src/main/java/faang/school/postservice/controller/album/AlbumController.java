package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.album.DeleteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
public class AlbumController {
    private final AlbumService service;

    @RequestMapping(method = RequestMethod.OPTIONS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PUT, DELETE, OPTIONS");

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public AlbumDto getAlbum(@PathVariable long id) {
        return service.getAlbum(id);
    }

    @PostMapping
    public AlbumDto createAlbum(@Validated @RequestBody AlbumDto albumDto) {
        return service.createAlbum(albumDto);
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable long albumId, @Validated @RequestBody AlbumDto updatedAlbum) {
        service.updateAlbum(albumId, updatedAlbum);
        return ResponseEntity.accepted().body(updatedAlbum);
    }

    @PostMapping("/{userId}/my-albums")
    public List<AlbumDto> getMyAlbums(@PathVariable long userId, @RequestBody AlbumFilterDto albumFilterDto) {
        return service.getMyAlbums(userId, albumFilterDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long id) {
        DeleteResult result = service.deleteAlbum(id);

        if (result == DeleteResult.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album not found.");
        } else if (result == DeleteResult.NOT_AUTHORIZED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to delete this album.");
        } else {
            return ResponseEntity.ok("Album deleted successfully.");
        }
    }

    @PostMapping("/{albumId}/{postId}")
    public AlbumDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        return service.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping("/{albumId}/{postIdToDelete}")
    public void deletePostFromAlbum(@PathVariable long albumId, @PathVariable long postIdToDelete) {
        service.deletePostFromAlbum(albumId, postIdToDelete);
    }
}
