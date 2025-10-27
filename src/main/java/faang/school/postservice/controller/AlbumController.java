package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
@Validated
public class AlbumController {
    private final AlbumService albumService;
    private final UserContext userContext;

    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@PathVariable Long albumId) {
        return albumService.getAlbum(albumId);
    }

    @GetMapping("/allAlbums")
    public List<AlbumDto> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @PostMapping("/update")
    public AlbumDto update(@RequestBody @Valid AlbumDto albumDto) {
        return albumService.update(albumDto);
    }

    @PutMapping("{albumId}/visibility")
    public AlbumDto updateVisibility(
            @PathVariable("albumId") Long albumId,
            @RequestParam("visibility") AlbumVisibility visibility,
            @RequestBody(required = false) List<Long> userIds) {
        return albumService.updateVisibility(albumId, visibility, userIds);
    }
}
