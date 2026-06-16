package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/albums")
@Validated
@Slf4j
public class AlbumController {

    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create album of posts ",
            description = "Creates user's album of posts"
    )
    public AlbumDto createAlbum(@Valid @RequestBody AlbumDto albumDto) {
        log.info("CREATE /album — request to create user's album of posts");
        return albumService.createAlbum(albumDto);
    }

    @PostMapping("/{albumId}/posts/{postId}")
    @Operation(
            summary = "Add post to album",
            description = "Adds an existing post to the specified user's album by post ID"
    )
    public AlbumDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        log.info("POST /album/{}/posts/{} — request to add post to album", albumId, postId);
        return albumService.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping("/{albumId}/posts/{postId}")
    @Operation(
            summary = "Remove post from album",
            description = "Removes a post from the specified user's album by post ID"
    )
    public AlbumDto removePostFromAlbum(@PathVariable("albumId") long albumId, @PathVariable("postId") long postId) {
        log.info("DELETE /album/{}/posts/{} — request to remove post from album", albumId, postId);
        return albumService.removePostFromAlbum(albumId, postId);
    }

    @PostMapping("/album/{albumId}/favorites")
    @Operation(
            summary = "Add album to favorites",
            description = "Adds the specified album to the current user's favorites"
    )
    public AlbumDto addAlbumToFavorite(@PathVariable("albumId") long albumId) {
        log.info("POST /album/{}/favorites — request to add album to favorites", albumId);
        return albumService.addAlbumToFavorite(albumId);
    }

    @DeleteMapping("/album/{albumId}/favorites")
    @Operation(
            summary = "Remove album from favorites",
            description = "Removes the specified album from the current user's favorites"
    )
    public AlbumDto removeAlbumFromFavorite(@PathVariable("albumId") long albumId) {
        log.info("DELETE /album/{}/favorites — request to remove album from favorites", albumId);
        return albumService.removeAlbumFromFavorite(albumId);
    }

    @GetMapping("/{albumId}")
    @Operation(
            summary = "Get album by ID",
            description = "Returns any album by its ID"
    )
    public AlbumDto getAlbumById(@PathVariable("albumId") long albumId) {
        log.info("GET /album/{} — request to get album by id", albumId);
        return albumService.getAlbumById(albumId);
    }

    @PostMapping("/filtered")
    @Operation(
            summary = "Get all user's albums with filters",
            description = "Returns a list of the current user's albums with optional filtering by title and date range"
    )
    public List<AlbumDto> getAllUserAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        log.info("POST /album/filtered — request to get user's albums with filters: {}", albumFilterDto);
        return albumService.getAllUserAlbums(albumFilterDto);
    }

    @PostMapping("/all")
    @Operation(
            summary = "Get all albums with filters",
            description = "Returns a list of all albums in the application with " +
                    "optional filtering by title and date range"
    )
    public List<AlbumDto> getAllAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        log.info("POST /album/all — request to get all albums with filters: {}", albumFilterDto);
        return albumService.getAllAlbums(albumFilterDto);
    }

    @PostMapping("/favorites")
    @Operation(
            summary = "Get all user's favorite albums with filters",
            description = "Returns a list of the current user's favorite albums with " +
                    "optional filtering by title and date range"
    )
    public List<AlbumDto> getAllUserFavoriteAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        log.info("POST /album/favorites — request to get user's favorite albums with filters: {}", albumFilterDto);
        return albumService.getAllUserFavoriteAlbums(albumFilterDto);
    }

    @PutMapping("/{albumId}")
    @Operation(
            summary = "Update album",
            description = "Updates the current user's existing album by its ID"
    )
    public AlbumDto updateAlbum(@PathVariable("albumId") long albumId,
                                @Valid @RequestBody AlbumDto albumDto) {
        log.info("PUT /album/{} — request to update album: {}", albumId, albumDto);
        return albumService.updateAlbum(albumId, albumDto);
    }

    @DeleteMapping("/{albumId}")
    @Operation(
            summary = "Delete album",
            description = "Deletes the current user's album by its ID and returns the deleted album"
    )
    public ResponseEntity<AlbumDto> deleteAlbum(@PathVariable("albumId") long albumId) {
        log.info("DELETE /album/{} — request to delete album", albumId);
        AlbumDto deletedAlbumDto = albumService.deleteAlbum(albumId);
        return ResponseEntity.ok(deletedAlbumDto);
    }
}
