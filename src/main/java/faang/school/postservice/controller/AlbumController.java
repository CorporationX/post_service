package faang.school.postservice.controller;

import faang.school.postservice.dto.album.*;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/albums")
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public AlbumDto createAlbum(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        return albumService.createAlbum(albumCreateDto);
    }

    @PostMapping("/{albumId}/posts/{postId}")
    public void addPostToAlbum(@NotNull @PathVariable Long albumId, @NotNull @PathVariable Long postId) {
        albumService.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping("/{albumId}/posts/{postId}")
    public void deletePostFromAlbum(@NotNull @PathVariable Long albumId, @NotNull @PathVariable Long postId) {
        albumService.deletePostFromAlbum(albumId, postId);
    }

    @PostMapping("/favorites/{albumId}")
    public void addAlbumToFavorites(@NotNull @PathVariable Long albumId) {
        albumService.addAlbumToFavorites(albumId);
    }

    @DeleteMapping("/favorites/{albumId}")
    public void deleteAlbumFromFavorites(@NotNull @PathVariable Long albumId) {
        albumService.deleteAlbumFromFavorites(albumId);
    }

    @PostMapping("/{albumId}")
    public AlbumDto findByWithPosts(@NotNull @PathVariable Long albumId) {
        return albumService.findByIdWithPosts(albumId);
    }

    @PostMapping("/filter/all")
    public List<AlbumDtoResponse> findAListOfAllYourAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.findAListOfAllYourAlbums(albumFilterDto);
    }

    @PostMapping("/filter/all/systems")
    public List<AlbumDtoResponse> findListOfAllAlbumsInTheSystem(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.findListOfAllAlbumsInTheSystem(albumFilterDto);
    }

    @PostMapping("/filter/all/favorites")
    public List<AlbumDtoResponse> findListOfAllYourFavorites(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.findAListOfAllYourFavoriteAlbums(albumFilterDto);
    }

    @PutMapping("/{albumId}")
    public AlbumDto updateAlbum(@NotNull @PathVariable Long albumId, @Valid @RequestBody AlbumUpdateDto albumUpdateDto) {
        return albumService.updateAlbumAuthor(albumId, albumUpdateDto);
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@NotNull @PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }
}
