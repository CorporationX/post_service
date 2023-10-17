package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumDtoResponse;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
@Tag(name = "Album", description = "Album API")
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping("/create")
    @Operation(summary = "Create Album")
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto album) {
        return albumService.createAlbum(album);
    }

    @PatchMapping("/update")
    @Operation(summary = "Update Album")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto updateAlbum(@RequestBody @Valid AlbumDto album) {
        validateIds(album.getId(), album.getAuthorId());
        return albumService.updateAlbum(album);
    }

    @DeleteMapping("/{albumId}")
    @Operation(summary = "Delete album")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }

    @GetMapping("/{albumId}")
    @Operation(summary = "Get Album by Id")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDtoResponse getAlbum(@PathVariable Long albumId) {
        validateIds(albumId);
        return albumService.getAlbum(albumId);
    }

    @PostMapping("/my/filter")
    @Operation(summary = "Get my Albums by Filter")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDtoResponse> getMyAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getMyAlbums(albumFilterDto);
    }

    @PostMapping("/my/favourites/filter")
    @Operation(summary = "Get my Favourites Albums by Filter")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDtoResponse> getMyFavouritesAlbums(@Valid @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getMyFavouritesAlbums(albumFilterDto);
    }

    @PostMapping("/filter")
    @Operation(summary = "Get Albums by Filter")
    @ResponseStatus(HttpStatus.OK)
    public List<AlbumDtoResponse> getAlbums(@Valid @RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAlbumsByFilter(albumFilterDto);
    }

    @PatchMapping("{albumId}/posts/add/{postId}")
    @Operation(summary = "Add post to album")
    public AlbumDto addPost(@PathVariable Long albumId, @PathVariable Long postId) {
        validateIds(albumId, postId);
        return albumService.addPost(albumId, postId);
    }

    @PatchMapping("{albumId}/posts/delete/{postId}")
    @Operation(summary = "Delete post from album")
    public AlbumDto deletePost(@PathVariable Long albumId, @PathVariable Long postId) {
        validateIds(albumId, postId);
        return albumService.deletePost(albumId, postId);
    }

    @PostMapping("/favourites/add/{albumId}")
    @Operation(summary = "Add album to favourites")
    public void addAlbumToFavourite(@PathVariable Long albumId) {
        validateIds(albumId);
        albumService.addAlbumToFavourite(albumId);
    }

    @DeleteMapping("/favourites/delete/{albumId}")
    @Operation(summary = "Delete album from favourites")
    public void deleteAlbumFromFavourite(@PathVariable Long albumId) {
        validateIds(albumId);
        albumService.deleteAlbumFromFavorites(albumId);
    }

    private void validateIds(long... ids) {
        Arrays.stream(ids).forEach(id -> {
            if (id < 0) {
                throw new DataValidationException("Id must be more than zero");
            }
        });
    }
}
