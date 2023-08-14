package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.album.DeleteResult;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/albums")
public class AlbumController {
    private final AlbumService service;

    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@PathVariable long albumId) {
        return service.getAlbum(albumId);
    }

    @GetMapping("/my-favorite-albums")
    public List<AlbumDto> getMyFavoriteAlbums(AlbumFilterDto filters) {
        return service.getMyFavoriteAlbums(filters);
    }

    @GetMapping
    public List<AlbumDto> findAllAlbums(AlbumFilterDto albumFilterDto) {
        return service.findAllAlbums(albumFilterDto);
    }

    @GetMapping("/my-albums")
    public List<AlbumDto> getMyAlbums(AlbumFilterDto albumFilterDto) {
        return service.getMyAlbums(albumFilterDto);
    }

    @PostMapping("/{albumId}/favorites")
    public ResponseEntity<String> addAlbumToFavorites(@PathVariable long albumId) {
        service.addAlbumToFavorites(albumId);
        return ResponseEntity.accepted().body("Album added to favorites");
    }

    @PostMapping
    public AlbumDto createAlbum(@Validated @RequestBody AlbumDto albumDto) {
        return service.createAlbum(albumDto);
    }

    @PostMapping("/{albumId}/{postId}")
    public AlbumDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        return service.addPostToAlbum(albumId, postId);
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable long albumId, @Validated @RequestBody AlbumDto updatedAlbum) {
        service.updateAlbum(albumId, updatedAlbum);
        return ResponseEntity.accepted().body(updatedAlbum);
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long albumId) {
        DeleteResult result = service.deleteAlbum(albumId);

        if (result == DeleteResult.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album not found.");
        } else if (result == DeleteResult.NOT_AUTHORIZED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to delete this album.");
        } else {
            return ResponseEntity.ok("Album deleted successfully.");
        }
    }

    @DeleteMapping("/{albumId}/{postIdToDelete}")
    public void deletePostFromAlbum(@PathVariable long albumId, @PathVariable long postIdToDelete) {
        service.deletePostFromAlbum(albumId, postIdToDelete);
    }

    @DeleteMapping("/{albumId}/favorites")
    public ResponseEntity<String> removeAlbumFromFavorites(@PathVariable long albumId) {
        service.removeAlbumFromFavorites(albumId);
        return ResponseEntity.accepted().body("Remove album from favorites");
    }
}
