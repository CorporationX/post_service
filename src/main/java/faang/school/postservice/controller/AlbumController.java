package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/album")
@RequiredArgsConstructor
@Validated
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public AlbumDto createAlbum(@Valid @RequestBody AlbumDto request) {
        return albumService.createAlbum(request);
    }

    @PostMapping("/users/{userId}/albums/{albumId}/post/{postId}")
    public void addPostInAlbum(
            @PathVariable @Positive Long albumId,
            @PathVariable @Positive Long postId,
            @PathVariable @Positive Long userId
    ) {
        albumService.addPostInAlbum(albumId, postId, userId);
    }

    @DeleteMapping("/users/{userId}/albums/{albumId}/post/{postId}")
    public void deletePostFromAlbum(
            @PathVariable @Positive Long albumId,
            @PathVariable @Positive Long postId,
            @PathVariable @Positive Long userId
    ) {
        albumService.deletePostFromAlbum(albumId, postId, userId);
    }

    @PostMapping("/users/{userId}/favorite-albums/{albumId}")
    public void addAlbumToFavorite(
            @PathVariable @Positive Long albumId,
            @PathVariable @Positive Long userId
    ) {
        albumService.addAlbumToFavorite(albumId, userId);
    }

    @DeleteMapping("/users/{userId}/favorite-albums/{albumId}")
    public void deleteAlbumFromFavorite(
            @PathVariable @Positive Long albumId,
            @PathVariable @Positive Long userId
    ) {
        albumService.deleteAlbumFromFavorite(albumId, userId);
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbumById(@PathVariable @Positive Long albumId) {
        return albumService.getAlbumById(albumId);
    }

    @GetMapping("/user/{userId}/albums")
    public List<AlbumDto> getAllUserAlbums(
            @PathVariable @Positive Long userId,
            @ModelAttribute AlbumFilterDto request
    ) {
        return albumService.getAllUserAlbums(userId, request);
    }

    @GetMapping("/all-albums")
    public List<AlbumDto> getAllAlbums(@ModelAttribute AlbumFilterDto request) {
        return albumService.getAllAlbums(request);
    }

    @GetMapping("/user/{userId}/favorite-albums")
    public List<AlbumDto> getAllFavoriteAlbums(
            @PathVariable @Positive Long userId,
            @ModelAttribute AlbumFilterDto request
    ) {
        return albumService.getAllFavoriteAlbums(userId, request);
    }

    @PutMapping
    public AlbumDto updateAlbum(@Valid @RequestBody AlbumDto request) {
        return albumService.updateAlbum(request);
    }

    @DeleteMapping("/users/{userId}/albums/{albumId}")
    public void deleteAlbum(
            @PathVariable @Positive Long albumId,
            @PathVariable @Positive Long userId
    ) {
        albumService.deleteAlbum(albumId, userId);
    }
}