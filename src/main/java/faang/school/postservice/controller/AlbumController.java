package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        return albumService.createAlbum(albumDto);
    }

    @PutMapping("{albumId}/posts/{postId}")
    public AlbumDto addPost(@PathVariable Long albumId, @PathVariable Long postId) {
        return albumService.addPost(albumId, postId);
    }

    @DeleteMapping("{albumId}/posts/{postId}")
    public void deletePost(@PathVariable Long albumId, @PathVariable Long postId) {
        albumService.deletePost(albumId, postId);
    }

    @PostMapping("/{albumId}/favorite")
    public void addToFavorite(@PathVariable Long albumId) {
        albumService.addToFavorite(albumId);
    }

    @DeleteMapping("/{albumId}/favorite")
    public void removeFromFavorite(@PathVariable Long albumId) {
        albumService.removeFromFavorite(albumId);
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@PathVariable Long albumId) {
        return albumService.getAlbum(albumId);
    }

    @GetMapping()
    public List<AlbumDto> getUserAlbums(@RequestBody AlbumFilterDto filterDto) {
        return albumService.getUserAlbums(filterDto);
    }

    @GetMapping("/all")
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto filterDto) {
        return albumService.getAllAlbums(filterDto);
    }

    @GetMapping("/favorite")
    public List<AlbumDto> getUserFavoriteAlbums(@RequestBody AlbumFilterDto filterDto) {
        return albumService.getUserFavoriteAlbums(filterDto);
    }

    @PutMapping("/{albumId}")
    public AlbumDto updateAlbum(@PathVariable Long albumId, @RequestBody AlbumDto albumDto) {
        return albumService.updateAlbum(albumId, albumDto);
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }
}
