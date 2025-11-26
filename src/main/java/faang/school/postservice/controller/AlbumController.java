package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.FavoriteAlbumsDto;
import faang.school.postservice.filters.AlbumFilterDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
@Validated
public class AlbumController {

    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@Valid @RequestBody AlbumDto albumDto) {
        Long userId = userContext.getUserId();
        return ResponseEntity.ok(albumService.createAlbum(albumDto, userId));
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable Long albumId,
                                                @Valid @RequestBody AlbumDto albumDto) {
        Long userId = userContext.getUserId();
        return ResponseEntity.ok(albumService.updateAlbum(albumId, albumDto, userId));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long albumId) {
        Long userId = userContext.getUserId();
        albumService.deleteAlbum(albumId, userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.getById(albumId));
    }

    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAlbums(AlbumFilterDto albumFilterDto) {
        List<AlbumDto> albums = albumService.getAlbums(albumFilterDto);

        return albums.isEmpty() ?
                ResponseEntity.noContent().build() : ResponseEntity.ok(albums);
    }

    @GetMapping("my")
    public ResponseEntity<List<AlbumDto>> getAuthorAlbums(AlbumFilterDto albumFilterDto) {
        Long userId = userContext.getUserId();

        return ResponseEntity.ok(albumService.getAuthorsAlbums(userId, albumFilterDto));
    }

    @PutMapping("/{album_id}")
    public ResponseEntity<AlbumDto> addPostToAlbum(@PathVariable("album_id") Long albumId,
                                                   @RequestParam("post_id") Long postId) {

        Long userId = userContext.getUserId();
        return ResponseEntity.ok(albumService.addPost(postId, albumId, userId));
    }

    @PutMapping("/{album_id}/favorite")
    public ResponseEntity<FavoriteAlbumsDto> addAlbumToFavorite(@PathVariable("album_id") Long albumId) {

        return ResponseEntity.ok(albumService.addToFavoriteAlbums(albumId, userContext.getUserId()));
    }

}