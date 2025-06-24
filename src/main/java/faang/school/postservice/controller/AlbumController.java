package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@Validated
public class AlbumController {

    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping()
    public AlbumDto createAlbum(@Valid @RequestBody AlbumDto albumDto) {
        return albumService.createAlbum(albumDto);
    }

    @PostMapping("/{albumId}/posts/{postId}")
    public AlbumDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        return albumService.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping("{albumId}/posts/{postId}")
    public AlbumDto removePostFromAlbum(@PathVariable("albumId") long albumId, @PathVariable("postId") long postId) {
        return albumService.removePostFromAlbum(albumId, postId);
    }

    @PostMapping("/add/album/{albumId}/favorites")
    public AlbumDto addAlbumToFavorite(@PathVariable("albumId") long albumId) {
        return albumService.addAlbumToFavorite(albumId);
    }

    @DeleteMapping("/album/{albumId}/favorites")
    public AlbumDto removeAlbumFromFavorite(@PathVariable("albumId") long albumId) {
        return albumService.removeAlbumFromFavorite(albumId);
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbumById(@PathVariable("albumId") long albumId) {
        return albumService.getAlbumById(albumId);
    }

    @PostMapping("/filtered")
    public List<AlbumDto> getAllUserAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllUserAlbums(albumFilterDto);
    }

    @PostMapping("/all")
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllAlbums(albumFilterDto);
    }

    @PostMapping("/favorites")
    public List<AlbumDto> getAllUserFavoriteAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllUserFavoriteAlbums(albumFilterDto);
    }

    @PutMapping("/{albumId}")
    public AlbumDto updateAlbum(@PathVariable("albumId") long albumId,
                                @Valid @RequestBody AlbumDto albumDto) {
        return albumService.updateAlbum(albumId, albumDto);
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<AlbumDto> deleteAlbum(@PathVariable("albumId") long albumId) {
        AlbumDto deletedAlbumDto = albumService.deleteAlbum(albumId);
        return ResponseEntity.ok(deletedAlbumDto);
    }
}
