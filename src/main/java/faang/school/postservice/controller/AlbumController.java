package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
import java.util.List;

import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
public class AlbumController {
    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping("")
    public void createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        albumDto.setAuthorId(userContext.getUserId());
        albumService.createAlbum(albumDto);
    }

    @PutMapping("/{id}")
    public void updateAlbum(@PathVariable Long id, @RequestBody @Valid AlbumDto albumDto) {
        if (albumDto.getAuthorId() != null) {
            throw new DataValidationException("Only author can modify albums");
        }
        albumDto.setAuthorId(userContext.getUserId());
        albumService.updateAlbum(id, albumDto);
    }

    @PutMapping("/{id}/post/{postId}")
    public void addPostToAlbum(@PathVariable Long id, @PathVariable Long postId) {
        albumService.addPostToAlbum(id, postId);
    }

    @DeleteMapping("/{id}/post/{postId}")
    public void deletePostFromAlbum(
            @PathVariable Long id,
            @PathVariable Long postId) {
        albumService.deletePostFromAlbum(id, postId);
    }

    @PostMapping("/{id}/favorite")
    public void addAlbumToFavorites(@PathVariable Long id) {
        Long userId = userContext.getUserId();
        albumService.addAlbumToFavorites(id, userId);
    }

    @DeleteMapping("/{id}/favorite")
    public void deleteAlbumFromFavorites(@PathVariable Long id) {
        Long userId = userContext.getUserId();
        albumService.deleteAlbumFromFavorites(id, userId);
    }

    @GetMapping("/{id}")
    public AlbumDto getAlbumById(@PathVariable Long id) {
        return albumService.getAlbumById(id);
    }

    @GetMapping("")
    public List<AlbumDto> getAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        Long authorId = userContext.getUserId();
        return albumService.getAlbums(authorId, albumFilterDto);
    }

    @GetMapping("/favorites")
    public List<AlbumDto> getFavoriteAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        Long userId = userContext.getUserId();
        return albumService.getFavoriteAlbums(userId, albumFilterDto);
    }

    @GetMapping("/all")
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllAlbums(albumFilterDto);
    }

    @DeleteMapping("/{id}")
    public void deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
    }
}
