package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.SubclassMapping;
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
@RequestMapping("/album")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/new")
    @Operation(summary = "Create new album", tags = "album")
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        return albumService.createAlbum(albumDto);
    }

    @PutMapping("{albumId}/posts/{postId}")
    @Operation(summary = "add post to album", tags = "post")
    public AlbumDto addPost(@PathVariable Long albumId, @PathVariable Long postId) {
        return albumService.addPost(albumId, postId);
    }

    @DeleteMapping("{albumId}/posts/{postId}")
    @Operation(summary = "delete post from album", tags = "post")
    public void deletePost(@PathVariable Long albumId, @PathVariable Long postId) {
        albumService.deletePost(albumId, postId);
    }

    @PostMapping("/{albumId}/favorite")
    @Operation(summary = "add album to favorite", tags = "album")
    public void addToFavorite(@PathVariable Long albumId) {
        albumService.addToFavorite(albumId);
    }

    @DeleteMapping("/{albumId}/favorite")
    @Operation(summary = "remove from favorite album", tags = "album")
    public void removeFromFavorite(@PathVariable Long albumId) {
        albumService.removeFromFavorite(albumId);
    }

    @GetMapping("/{albumId}")
    @Operation(summary = "get album", tags = "album")
    public AlbumDto getAlbum(@PathVariable Long albumId) {
        return albumService.getAlbum(albumId);
    }

    @GetMapping()
    @Operation(summary = "get filtered user's albums", tags = "album")
    public List<AlbumDto> getUserAlbums(@RequestBody AlbumFilterDto filterDto) {
        return albumService.getUserAlbums(filterDto);
    }

    @GetMapping("/all")
    @Operation(summary = "get all filtered albums", tags = "album")
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto filterDto) {
        return albumService.getAllAlbums(filterDto);
    }

    @GetMapping("/favorite")
    @Operation(summary = "get filtered user's favorite albums", tags = "album")
    public List<AlbumDto> getUserFavoriteAlbums(@RequestBody AlbumFilterDto filterDto) {
        return albumService.getUserFavoriteAlbums(filterDto);
    }

    @PutMapping("/{albumId}")
    @Operation(summary = "update album", tags = "album")
    public AlbumDto updateAlbum(@PathVariable Long albumId, @RequestBody AlbumDto albumDto) {
        return albumService.updateAlbum(albumId, albumDto);
    }

    @DeleteMapping("/{albumId}")
    @Operation(summary = "delete album", tags = "album")
    public void deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }
}
