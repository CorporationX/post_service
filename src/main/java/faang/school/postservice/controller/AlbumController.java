package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
       // userContext.setUserId(12);
        return albumService.createAlbum(albumDto);
    }

    @PostMapping("{albumId}/posts/{postId}")
    public AlbumDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        long userId = userContext.getUserId();
        return albumService.addPostToAlbum(albumId, postId, userId);
    }

    @DeleteMapping("{albumId}/posts/{postId}")
    public AlbumDto removePostFromAlbum(@PathVariable("albumId") long albumId, @PathVariable("postId") long postId) {
        long userId = userContext.getUserId();
        return albumService.removePostFromAlbum(albumId, postId, userId);
    }

    @PostMapping("/add/album/{albumId}/favorites")
    public AlbumDto addAlbumToFavorite(@PathVariable("albumId") long albumId) {
        long userId = userContext.getUserId();
        return albumService.addAlbumToFavorite(albumId, userId);
    }

    @DeleteMapping("/album/{albumId}/favorites")
    public AlbumDto removeAlbumFromFavorite(@PathVariable("albumId") long albumId) {
        long userId = userContext.getUserId();
        return albumService.removeAlbumFromFavorite(albumId, userId);
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbumById(@PathVariable("albumId") long albumId) {
        return albumService.getAlbumById(albumId);
    }

    @PostMapping("/filtered")
    public List<AlbumDto> getAllUserAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        return albumService.getAllUserAlbums(userId, albumFilterDto);
    }

    @PostMapping("/all")
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllAlbums(albumFilterDto);
    }

    @PostMapping("/favorites")
    public List<AlbumDto> getAllUserFavoriteAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        return albumService.getAllUserFavoriteAlbums(userId, albumFilterDto);
    }

    @PutMapping("/{albumId}")
    public AlbumDto updateAlbum(@PathVariable("albumId") long albumId, @RequestBody AlbumDto albumDto) {
        long userId = userContext.getUserId();
        return albumService.updateAlbum(albumId, userId, albumDto);
    }

    @DeleteMapping("/{albumId}")
    public AlbumDto deleteAlbum(@PathVariable("albumId") long albumId){
        long userId = userContext.getUserId();
        return albumService.deleteAlbum(albumId, userId);
    }
}
