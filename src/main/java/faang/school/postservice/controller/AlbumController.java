package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
@Tag(name = "Internship Management", description = "API for managing albums")
@Validated
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping()
    @Operation(summary = "Create an album", description = "Create a new empty album")
    public AlbumDto create(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Valid @RequestBody AlbumDto albumDto) {
        return albumService.create(albumDto);
    }

    @PostMapping("/{albumId}/posts/{postId}")
    @Operation(summary = "Add a post", description = "Add a post to the album")
    public String addPostToAlbum(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the album", required = true)
            @PathVariable Long albumId,
            @Parameter(description = "ID of the post to add to the album", required = true)
            @PathVariable Long postId) {
        return albumService.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping("/{albumId}/posts/{postId}")
    @Operation(summary = "Delete a post", description = "Delete a post from the album")
    public AlbumDto deletePostFromAlbum(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the album", required = true)
            @PathVariable Long albumId,
            @Parameter(description = "ID of the post to delete from the album", required = true)
            @PathVariable Long postId) {
        return albumService.deletePostFromAlbum(albumId, postId);
    }

    @PostMapping("/{albumId}/favorites")
    @Operation(summary = "Add an album to favorites", description = "Add an album to favorites")
    public String addAlbumToFavorites(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the album to add to favorites", required = true)
            @PathVariable Long albumId) {
        return albumService.addAlbumToFavorites(albumId);
    }

    @DeleteMapping("/{albumId}/favorites")
    @Operation(summary = "Delete album from favorites", description = "Delete album from favorites")
    public String deleteAlbumFromFavorites(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the album to delete", required = true)
            @PathVariable Long albumId) {
        return albumService.deleteAlbumFromFavorites(albumId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an album by ID", description = "Retrieve an album by its ID.")
    public AlbumDto getAlbumById(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the album", required = true)
            @PathVariable Long id) {
        return albumService.getAlbumById(id);
    }

    @PostMapping("/user/filter")
    @Operation(summary = "Get user's albums by filter", description = "Retrieve user's albums by filter")
    public List<AlbumDto> getUserAlbums(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "Filter for user's albums", required = true)
            @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getUserAlbums(albumFilterDto);
    }

    @PostMapping("/filter")
    @Operation(summary = "Get all users albums by filter", description = "Retrieve all user's albums by filter")
    public List<AlbumDto> getAllUsersAlbums(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "Filter for all users albums", required = true)
            @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAllUsersAlbums(albumFilterDto);
    }

    @PostMapping("/user/favorite/filter")
    @Operation(summary = "Get user's favorite albums by filter", description = "Retrieve user's favorite albums by filter")
    public List<AlbumDto> getUserFavoriteAlbums(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "Filter for user's favorite albums", required = true)
            @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getUserFavoriteAlbums(albumFilterDto);
    }

    @PutMapping("/user/{albumId}")
    @Operation(summary = "Update user's album", description = "Update an existing user's album")
    public AlbumDto update(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @PathVariable @NotNull Long albumId,
            @Valid @RequestBody AlbumDto albumDto) {
        return albumService.update(albumId, albumDto);
    }

    @DeleteMapping("/user/{albumId}")
    @Operation(summary = "Delete user's album", description = "Delete an existing user's album")
    public void delete(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @PathVariable @NotNull Long albumId) {
        albumService.delete(albumId);
    }
}

