package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
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
@RequestMapping("/api/v1/album")
public class AlbumController {
    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping
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
        Long userId = userContext.getUserId();
        return albumService.getAlbumById(id, userId);
    }

    @GetMapping("")
    public List<AlbumDto> getAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        Long authorId = userContext.getUserId();
        return albumService.getAlbums(authorId, albumFilterDto);
    }

    @GetMapping("/albums/favorite")
    public List<AlbumDto> getFavoriteAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        Long userId = userContext.getUserId();
        return albumService.getFavoriteAlbums(userId, albumFilterDto);
    }

    @GetMapping("/all")
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        Long userId = userContext.getUserId();
        return albumService.getAllAlbums(albumFilterDto, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
    }
}
