package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
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

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService service;
    private final AlbumMapper albumMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        Album album = albumMapper.toEntity(albumDto);
        Album saveAlbum = service.createAlbum(album);
        return albumMapper.toDto(saveAlbum);
    }

    @PutMapping("/{album-id}/add-post")
    public AlbumDto addPostToAlbum(@RequestParam long postId,
                                   @PathVariable("album-id") long albumId,
                                   @RequestParam long userId) {
        Album saveAlbum = service.addPostToAlbum(postId, albumId, userId);
        return albumMapper.toDto(saveAlbum);
    }

    @PutMapping("/{album-id}/remove-post")
    public AlbumDto removePostFromAlbum(@RequestParam long postId,
                                        @PathVariable("album-id") long albumId,
                                        @RequestParam long userId) {
        Album saveAlbum = service.removePostFromAlbum(postId, albumId, userId);
        return albumMapper.toDto(saveAlbum);
    }

    @PutMapping("/{album-id}/favorite")
    public void addAlbumToFavorite(@PathVariable("album-id") long albumId, @RequestParam long userId) {
        service.addAlbumToFavorite(albumId, userId);
    }

    @PutMapping("/{album-id}/unfavorite")
    public void removeAlbumFromFavorite(@PathVariable("album-id") long albumId, @RequestParam long userId) {
        service.removeAlbumFromFavorite(albumId, userId);
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@PathVariable("album-id") long albumId) {
        Album album = service.getAlbum(albumId);
        return albumMapper.toDto(album);
    }

    @PutMapping("/{album-id}")
    public AlbumDto updateAlbum(@PathVariable("album-id") long albumId,
                                @RequestParam long authorId,
                                @RequestBody @Valid AlbumDto albumDto) {
        Album album = albumMapper.toEntity(albumDto);
        Album modifiedAlbum = service.updateAlbum(albumId, authorId, album);
        return albumMapper.toDto(modifiedAlbum);
    }

    @DeleteMapping("/{album-id}")
    public void deleteAlbum(@PathVariable("album-id") long albumId, @RequestParam long userId) {
        service.deleteAlbum(albumId, userId);
    }

    @PostMapping("/filter")
    public List<AlbumDto> getAlbumsByFilter(@RequestBody AlbumFilterDto filterDto) {
        List<Album> albums = service.getAlbumsByFilter(filterDto);
        return albumMapper.toDtoList(albums);
    }

    @PostMapping("/by-user")
    public List<AlbumDto> getUserAlbumsByFilter(@RequestParam Long userId, @RequestBody AlbumFilterDto filterDto) {
        List<Album> albums = service.getUserAlbumsByFilters(userId, filterDto);
        return albumMapper.toDtoList(albums);
    }

    @PostMapping("/favorite-by-user")
    public List<AlbumDto> getFavoriteUserAlbumsByFilter(@RequestParam Long userId, @RequestBody AlbumFilterDto filterDto) {
        List<Album> albums = service.getFavoriteUserAlbumsByFilters(userId, filterDto);
        return albumMapper.toDtoList(albums);
    }
}