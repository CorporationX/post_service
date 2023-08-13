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
        validate(album);
        return albumService.createAlbum(album);
    }

    @PatchMapping("/update")
    @Operation(summary = "Update Album")
    @ResponseStatus(HttpStatus.OK)
    public AlbumDto updateAlbum(@RequestBody @Valid AlbumDto album) {
        validate(album);
        return albumService.updateAlbum(album);
    }

    @DeleteMapping("/{albumId}")
    @Operation(summary = "Delete Album")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }

    private void validate(AlbumDto album) {
        if (album.getAuthorId() == null) {
            throw new DataValidationException("AuthorId cannot be null");
        }
        if (album.getTitle() == null || album.getTitle().isEmpty()) {
            throw new DataValidationException("Title cannot be null");
        }
        if (album.getDescription() == null || album.getDescription().isEmpty()) {
            throw new DataValidationException("Description cannot be null");
        }
    }
}
