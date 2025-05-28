package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PostMapping
    public AlbumUpdateDto createAlbum(@RequestBody @Valid AlbumCreateDto albumCreateDto) {
        return albumService.createAlbum(albumCreateDto);
    }

    @PutMapping("/{albumId}")
    public AlbumUpdateDto updateAlbum(@PathVariable Long albumId, @RequestBody @Valid AlbumUpdateDto albumUpdateDto) {
        return albumService.updateAlbum(albumId, albumUpdateDto);
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@PathVariable Long albumId, @RequestParam Long userId) {
        albumService.deleteAlbum(albumId, userId);
    }

    @PatchMapping("/{albumId}/posts")
    public AlbumUpdateDto addPostsToAlbum(@PathVariable Long albumId, @RequestBody @Valid AlbumUpdateDto albumWithPosts) {
        return albumService.addPostsToAlbum(albumId, albumWithPosts);
    }

    @DeleteMapping("/{albumId}/posts")
    public void deletePostsFromAlbum(@PathVariable Long albumId, @RequestBody @Valid AlbumUpdateDto albumUpdateDto) {
        albumService.deletePostsFromAlbum(albumId, albumUpdateDto);
    }

    @PostMapping("/user/{userId}/favorites")
    public void addAlbumToFavorites(@PathVariable Long userId, @RequestParam Long albumId) {
        albumService.addAlbumToFavorites(userId, albumId);
    }

    @DeleteMapping("/user/{userId}/favorites")
    public void deleteAlbumFromFavorites(@PathVariable Long userId, @RequestParam Long albumId) {
        albumService.deleteAlbumFromFavorites(userId, albumId);
    }

    @GetMapping("/{albumId}")
    public AlbumUpdateDto getAlbumById(@PathVariable Long albumId) {
        return albumService.getAlbumById(albumId);
    }

    @GetMapping("/user/{userId}")
    public List<AlbumUpdateDto> getUserAlbumsWithFilters(@PathVariable Long userId, @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getUserAlbumsWithFilters(userId, albumFilterDto);
    }

    @GetMapping
    public List<AlbumUpdateDto> getAllAlbumsWithFilters(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllAlbumsWithFilters(albumFilterDto);
    }

    @GetMapping("/{userId}/favorites")
    public List<AlbumUpdateDto> getAllFavoritesUserAlbumsWithFilters(@PathVariable Long userId, @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllFavoritesUserAlbumsWithFilters(userId, albumFilterDto);
    }
}