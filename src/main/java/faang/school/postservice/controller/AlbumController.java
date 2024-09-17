package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService service;
    private final AlbumMapper albumMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto albumDto){
        Album album = albumMapper.toEntity(albumDto);
        Album saveAlbum = service.createAlbum(album);
        return albumMapper.toDto(saveAlbum);
    }

    @PutMapping("/{albumId}/favorite")
    public void addAlbumToFavorite(@PathVariable long albumId, @RequestParam long userId) {
        service.addAlbumToFavorite(albumId, userId);
    }

    @DeleteMapping("/{albumId}/favorite")
    public void removeAlbumToFavorite(@PathVariable long albumId, @RequestParam long userId) {
        service.removeAlbumToFavorite(albumId, userId);
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@PathVariable long albumId) {
        Album album = service.getAlbum(albumId);
        return albumMapper.toDto(album);
    }
}