package faang.school.postservice.controller.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping("/create")
    public void createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        long userId = userContext.getUserId();
        albumService.createAlbum(albumDto, userId);
    }

    @DeleteMapping("/delete/{albumId}")
    public void deleteAlbum(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.deleteAlbum(albumId, userId);
    }

    @PostMapping("/addPost/{albumId}/{postId}")
    public void addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        long userId = userContext.getUserId();
        albumService.addPostToAlbum(albumId, postId, userId);
    }

    @PostMapping("/addToFavorite/{albumId}")
    public void addToFavorite(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.addToFavorite(albumId, userId);
    }

    @DeleteMapping("/removeFromFavorite/{albumId}")
    public void removeFromFavorite(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.removeFromFavorite(albumId, userId);
    }

    @GetMapping("/get/{albumId}")
    public AlbumDto getAlbum(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        return albumService.getAlbum(albumId, userId);
    }

    @GetMapping("/get/allUserAlbums")
    public List<AlbumDto> getAllUserAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        return albumService.getAllUserAlbums(userId, albumFilterDto);
    }

    @GetMapping("/get/allAlbums")
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllAlbums(albumFilterDto);
    }

    @GetMapping("/get/allFavorites")
    public List<AlbumDto> getAllFavorites(@RequestBody AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        return albumService.getAllFavorites(userId, albumFilterDto);
    }

    @PostMapping("/update")
    public void updateAlbum(@RequestBody AlbumDto albumDto) {
        albumService.updateAlbum(albumDto);
    }
}