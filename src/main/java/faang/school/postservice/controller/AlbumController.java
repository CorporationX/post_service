package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/new")
    public ResponseEntity<AlbumDto> createAlbum(
            @RequestHeader(value = "x-user-id") Long userId,
            @Valid @RequestBody AlbumDto albumDto) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        AlbumDto savedDto = albumService.createAlbum(userId, albumDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @PostMapping("/{albumId}/add-post/{postId}")
    public ResponseEntity<AlbumDto> addPostToAlbum(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long postId,
            @PathVariable Long albumId) {
        AlbumDto savedDto = albumService.addPostToAlbum(userId, postId, albumId);
        return ResponseEntity.ok(savedDto);
    }

    @DeleteMapping("/{albumId}/delete-post/{postId}")
    public ResponseEntity<Void> deletePostFromAlbum(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long postId,
            @PathVariable Long albumId) {
        albumService.deletePostFromAlbum(userId, postId, albumId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumByIdReturnAlbumDto(
            @PathVariable long albumId) {
        AlbumDto albumDto = albumService.getAlbumByIdReturnAlbumDto(albumId);
        return ResponseEntity.ok(albumDto);
    }

    @PutMapping("/update/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(
            @RequestHeader("x-user-id") Long userId,
            @RequestBody AlbumDto albumDto) {
        AlbumDto updatedAlbum = albumService.updateAlbum(userId, albumDto);
        return ResponseEntity.ok(updatedAlbum);
    }

    @DeleteMapping("/delete/{albumId}")
    public ResponseEntity<Void> deleteAlbum(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long albumId) {
        albumService.deleteAlbum(userId, albumId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/by-author/{userId}")
    public ResponseEntity<List<AlbumDto>> getAllAlbumsByAuthorIdWithFilters(
            @PathVariable Long userId,
            @RequestBody AlbumFilterDto albumFilterDto) {
        List<AlbumDto> albums = albumService.getAllAlbumsByAuthorIdWithFilters(userId, albumFilterDto);
        return ResponseEntity.ok(albums);
    }

    @PostMapping("/all-albums")
    public ResponseEntity<List<AlbumDto>> getAllAlbumsWithFilters(
            @RequestBody AlbumFilterDto albumFilterDto) {
        List<AlbumDto> albums = albumService.getAllAlbumsWithFilters(albumFilterDto);
        return ResponseEntity.ok(albums);
    }

    @PostMapping("/favourite/add/{albumId}")
    public ResponseEntity<Map<String, Boolean>> addFavouriteAlbum(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long albumId) {
        albumService.addFavouriteAlbum(userId, albumId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/favourite/delete/{albumId}")
    public ResponseEntity<Void> deleteFavouriteAlbum(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable Long albumId) {
        albumService.deleteFavouriteAlbum(userId, albumId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/favourite")
    public ResponseEntity<List<AlbumDto>> getFavouriteAlbumsByUserId(
            @RequestHeader("x-user-id") Long userId,
            @RequestBody AlbumFilterDto albumFilterDto) {
        List<AlbumDto> albums = albumService.getFavouriteAlbumsByUserId(userId, albumFilterDto);
        return ResponseEntity.ok(albums);
    }

}
