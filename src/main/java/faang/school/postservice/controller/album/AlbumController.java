package faang.school.postservice.controller.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
@Tag(name = "Albums", description = "Endpoints for managing albums")
public class AlbumController {
    private final AlbumService albumService;
    private final UserContext userContext;

    @Operation(summary = "Create an album")
    @PostMapping
    public AlbumDto create(@Valid @RequestBody AlbumDto albumDto) {
        return albumService.create(albumDto);
    }

    @Operation(summary = "Get filtered list of user's albums")
    @PostMapping("/user/filtered")
    public List<AlbumDto> getUsersAlbums(@RequestBody AlbumFilterDto filters) {
        long userId = userContext.getUserId();
        return albumService.getUsersAlbums(userId, filters);
    }

    @Operation(summary = "Get filtered list of every existing album")
    @PostMapping("/filtered")
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto filters) {
        return albumService.getAllAlbums(filters);
    }

    @Operation(summary = "Get filtered list of user's favourite albums")
    @PostMapping("/user/favourite/filtered")
    public List<AlbumDto> getFavouriteAlbums(@RequestBody AlbumFilterDto filters) {
        long userId = userContext.getUserId();
        return albumService.getFavouriteAlbums(userId, filters);
    }

    @Operation(summary = "Update existing album")
    @PutMapping
    public AlbumDto update(@Valid @RequestBody AlbumDto albumDto) {
        long userId = userContext.getUserId();
        return albumService.update(userId, albumDto);
    }

    @Operation(summary = "Add post to an album")
    @PutMapping("/{albumId}/post/{postId}")
    public AlbumDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        long userId = userContext.getUserId();
        return albumService.addPostToAlbum(userId, albumId, postId);
    }

    @Operation(summary = "Add album to favourites")
    @PutMapping("/favourite")
    public void addAlbumToFavourites(@Valid @RequestBody AlbumDto albumDto) {
        long userId = userContext.getUserId();
        albumService.addAlbumToFavourites(userId, albumDto);
    }

    @Operation(summary = "Delete post from an album")
    @DeleteMapping("/{albumId}/post/{postId}")
    public AlbumDto deletePostFromAlbum(@PathVariable long albumId, @PathVariable long postId) {
        long userId = userContext.getUserId();
        return albumService.deletePostFromAlbum(userId, albumId, postId);
    }

    @Operation(summary = "Delete album from favourites")
    @DeleteMapping("/favourite")
    public void deleteAlbumFromFavourites(@Valid @RequestBody AlbumDto albumDto) {
        long userId = userContext.getUserId();
        albumService.deleteAlbumFromFavourites(userId, albumDto);
    }

    @Operation(summary = "Delete an album")
    @DeleteMapping("/{albumId}")
    public void delete(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.delete(userId, albumId);
    }
}
