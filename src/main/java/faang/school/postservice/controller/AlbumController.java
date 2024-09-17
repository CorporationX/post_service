package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PutMapping("/{albumId}")
    public AlbumDto addPostToAlbum(@RequestParam long postId, @PathVariable long albumId, @RequestParam long userId) {
        Album saveAlbum = service.addPostToAlbum(postId, albumId, userId);
        return albumMapper.toDto(saveAlbum);
    }

    @DeleteMapping("/{albumId}")
    public AlbumDto removePostFromAlbum(@RequestParam long postId, @PathVariable long albumId, @RequestParam long userId) {
        Album saveAlbum = service.removePostFromAlbum(postId, albumId, userId);
        return albumMapper.toDto(saveAlbum);
    }

}