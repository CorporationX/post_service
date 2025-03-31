package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/albums")
@Validated
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(@Qualifier("albumServiceImpl") AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> findAlbumById(
            @PathVariable @NotNull @Min(1) long id
    ) {
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<AlbumResponseDto>> findAlbumsByAuthorId(
            @PathVariable @NotNull @Min(1) long authorId
    ) {
        return ResponseEntity.ok(albumService.getAlbumsByAuthorId(authorId));
    }

    @PutMapping("/{id}/visibility/{visibility}")
    public ResponseEntity<AlbumResponseDto> updateAlbumVisibility(
            @PathVariable @NotNull @Min(1) long id,
            @PathVariable("visibility") @NotNull AlbumVisibility visibility
    ) {
        AlbumResponseDto updatedAlbum = albumService.updateAlbumVisibility(id, visibility);
        return ResponseEntity.ok(updatedAlbum);
    }

    @PutMapping("/{id}/add-users-for-access")
    public ResponseEntity<List<Long>> addUsersForAccessAlbum(
            @PathVariable @NotNull @Min(1) long id,
            @RequestBody @Valid AlbumUsersDto albumUsersDto
    ) {
        List<Long> updatedUsers = albumService.addUsersForAccessAlbum(id, albumUsersDto);
        return ResponseEntity.ok(updatedUsers);
    }
}
