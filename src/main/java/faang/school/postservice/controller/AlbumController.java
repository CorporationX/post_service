package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping
    public AlbumReadDto createAlbum(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        return albumService.createAlbum(userContext.getUserId(), albumCreateDto);
    }

    @GetMapping("/{albumId}")
    public AlbumReadDto getAlbumById(@PathVariable long albumId) {
        return albumService.getAlbumById(albumId);
    }

    @GetMapping
    public List<AlbumReadDto> getAllAlbums(@Valid @RequestBody AlbumFilterDto filter) {
        return albumService.getAllAlbums(filter);
    }

    @GetMapping("/users")
    public List<AlbumReadDto> getUserAlbums(@Valid @RequestBody AlbumFilterDto filter) {
        return albumService.getUserAlbums(userContext.getUserId(), filter);
    }

    @PatchMapping
    public AlbumReadDto updateAlbum(@Valid @RequestBody AlbumUpdateDto albumUpdateDto) {
        return albumService.updateAlbum(userContext.getUserId(), albumUpdateDto.getId(), albumUpdateDto);
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@PathVariable long albumId) {
        albumService.deleteAlbum(userContext.getUserId(), albumId);
    }

    @PostMapping("/{albumId}/posts/{postId}")
    public AlbumReadDto addPostToAlbum(@PathVariable long albumId, @PathVariable long postId) {
        return albumService.addPostToAlbum(userContext.getUserId(), albumId, postId);
    }

    @DeleteMapping("/{albumId}/posts/{postId}")
    public AlbumReadDto removePostFromAlbum(@PathVariable long albumId, @PathVariable long postId) {
        return albumService.removePostFromAlbum(userContext.getUserId(), albumId, postId);
    }

    @PostMapping("/{albumId}/favorites")
    public void addAlbumToFavorites(@PathVariable long albumId) {
        albumService.addAlbumToFavorites(userContext.getUserId(), albumId);
    }

    @DeleteMapping("/{albumId}/favorites")
    public void removeAlbumFromFavorites(@PathVariable long albumId) {
        albumService.removeAlbumFromFavorites(userContext.getUserId(), albumId);
    }

    @GetMapping("/favorites")
    public List<AlbumReadDto> getFavoriteAlbums(AlbumFilterDto filter) {
        return albumService.getFavoriteAlbums(userContext.getUserId(), filter);
    }


}
