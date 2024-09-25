package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1")
public class AlbumController {
    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping("/albums")
    public ResponseEntity<Void> createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        albumDto.setAuthorId(userContext.getUserId());
        albumService.createAlbum(albumDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/albums/{id}")
    public ResponseEntity<Void> updateAlbum(
            @PathVariable Long id,
            @RequestBody @Valid AlbumDto albumDto) {
        if (albumDto.getAuthorId() != null) {
            throw new DataValidationException("Only author can modify albums");
        }
        albumDto.setAuthorId(userContext.getUserId());
        albumService.updateAlbum(id, albumDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/albums/{id}/posts/{postId}")
    public ResponseEntity<Void> addPostToAlbum(
            @PathVariable Long id,
            @PathVariable Long postId) {
        albumService.addPostToAlbum(id, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/albums/{id}/posts/{postId}")
    public ResponseEntity<Void> deletePostFromAlbum(
            @PathVariable Long id,
            @PathVariable Long postId) {
        albumService.deletePostFromAlbum(id, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/albums/{id}/favorites")
    public ResponseEntity<Void> addAlbumToFavorites(@PathVariable Long id) {
        Long userId = userContext.getUserId();
        albumService.addAlbumToFavorites(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/albums/{id}/favorites")
    public ResponseEntity<Void> deleteAlbumFromFavorites(@PathVariable Long id) {
        Long userId = userContext.getUserId();
        albumService.deleteAlbumFromFavorites(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/albums/{id}")
    public ResponseEntity<AlbumDto> getAlbumById(@PathVariable Long id) {
        AlbumDto albumDto = albumService.getAlbumById(id);
        return new ResponseEntity<>(albumDto, HttpStatus.OK);
    }

    @GetMapping("/albums")
    public ResponseEntity<List<AlbumDto>> getAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        Long authorId = userContext.getUserId();
        List<AlbumDto> albumDtoList = albumService.getAlbums(authorId, albumFilterDto);
        return new ResponseEntity<>(albumDtoList, HttpStatus.OK);
    }

    @GetMapping("/albums/favorites")
    public ResponseEntity<List<AlbumDto>> getFavoriteAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        Long userId = userContext.getUserId();
        List<AlbumDto> albumDtoList = albumService.getFavoriteAlbums(userId, albumFilterDto);
        return new ResponseEntity<>(albumDtoList, HttpStatus.OK);
    }

    @GetMapping("/albums/all")
    public ResponseEntity<List<AlbumDto>> getAllAlbums(@RequestBody AlbumFilterDto albumFilterDto) {
        List<AlbumDto> albumDtoList = albumService.getAllAlbums(albumFilterDto);
        return new ResponseEntity<>(albumDtoList, HttpStatus.OK);
    }

    @DeleteMapping("/albums/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
