package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

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
    @Operation(summary = "Delete Album")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }

    @PatchMapping("{albumId}/posts/add/{postId}")
    public AlbumDto addPost(@PathVariable Long albumId, @PathVariable Long postId) {
        validateIds(albumId, postId);
        return albumService.addPost(albumId, postId);
    }

    @PatchMapping("{albumId}/posts/delete/{postId}")
    public AlbumDto deletePost(@PathVariable Long albumId, @PathVariable Long postId) {
        validateIds(albumId, postId);
        return albumService.deletePost(albumId, postId);
    }

    private void validateIds(long... ids) {
        Arrays.stream(ids).forEach(id -> {
            if (id < 0) {
                throw new DataValidationException("Id must be more than zero");
            }
        });
    }
}
