package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Month;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/albums")
@Tag(name = "Album API", description = "Endpoints for managing post albums.")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    @Operation(summary = "Create album.")
    public ResponseEntity<AlbumDto> createAlbum(@Valid @RequestBody AlbumDto albumDto) {
        log.info("Creating album {} by UserId {}", albumDto.getTitle(), albumDto.getAuthorId());

        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(albumDto));
    }

    @PostMapping("/{albumId}/users/{userId}/posts/{postId}")
    @Operation(summary = "Add post to album.")
    public ResponseEntity<AlbumDto> addPostToAlbum(
            @PathVariable @Positive(message = "AlbumId must be greater than 0.")
            @NotNull(message = "AlbumId must not be null.") long albumId,
            @PathVariable @Positive(message = "UserId must be greater than 0.")
            @NotNull(message = "UserId must not be null.") long userId,
            @PathVariable @Positive(message = "PostId must be greater than 0.")
            @NotNull(message = "PostId must be greater than 0.") long postId) {
        log.info("Adding post #{} to album. UserId #{}, AlbumId #{}", postId, userId, albumId);

        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.addPostToAlbum(userId, albumId, Collections.singletonList(postId)));
    }

    @DeleteMapping("/{albumId}/users/{userId}/posts/{postId}")
    @Operation(summary = "Remove post from album.")
    public ResponseEntity<AlbumDto> removePostFromAlbum(
            @PathVariable @Positive(message = "AlbumId must be greater than 0.")
            @NotNull(message = "AlbumId must not be null.") long albumId,
            @PathVariable @Positive(message = "UserId must be greater than 0.")
            @NotNull(message = "UserId must not be null.") long userId,
            @PathVariable @Positive(message = "PostId must be greater than 0.")
            @NotNull(message = "PostId must be greater than 0.") long postId) {
        log.info("Removing post #{} from album. UserId #{}, AlbumId #{}", postId, userId, albumId);

        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.removePostFromAlbum(userId, albumId, postId));
    }

    @PostMapping("/{albumId}/users/{userId}/favorites")
    @Operation(summary = "Add album to user's favorites.")
    public ResponseEntity<AlbumDto> addAlbumToFavorites(
            @PathVariable @Positive(message = "AlbumId must be greater than 0.")
            @NotNull(message = "AlbumId must not be null.") long albumId,
            @PathVariable @Positive(message = "UserId must be greater than 0.")
            @NotNull(message = "UserId must not be null.") long userId) {
        log.info("Adding album to favorites. UserId #{}, AlbumId #{}", userId, albumId);

        return ResponseEntity.ok(albumService.addAlbumToFavorites(userId, albumId));
    }

    @DeleteMapping("/{albumId}/users/{userId}/favorites")
    @Operation(summary = "Remove album from favorites.")
    public ResponseEntity<Void> deleteAlbumFromFavorites(
            @PathVariable @Positive(message = "AlbumId must be greater than 0.")
            @NotNull(message = "AlbumId must not be null.") long albumId,
            @PathVariable @Positive(message = "UserId must be greater than 0.")
            @NotNull(message = "UserId must not be null.") long userId) {
        log.info("Removing album from favorites. UserId #{}, AlbumId #{}", userId, albumId);
        albumService.deleteAlbumFromFavorites(userId, albumId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{albumId}")
    @Operation(summary = "Get album by id.")
    public ResponseEntity<AlbumDto> getAlbumById(
            @PathVariable @Positive(message = "AlbumId must be greater than 0.")
            @NotNull(message = "AlbumId must not be null.") long albumId) {
        log.info("Fetching album by id #{}", albumId);

        return ResponseEntity.ok(albumService.findByAlbumId(albumId));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user's albums filtered by title, description and month.")
    public ResponseEntity<List<AlbumDto>> getUserAlbumsWithFilters(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.") long userId,
            @RequestParam(required = false) String titlePattern,
            @RequestParam(required = false) String descriptionPattern,
            @RequestParam(required = false) String month) {

        AlbumFilterDto filterDto = AlbumFilterDto.builder()
                .titlePattern(titlePattern)
                .descriptionPattern(descriptionPattern)
                .month(Month.valueOf(month))
                .build();
        log.info("Fetching albums for user #{} with filters {}", userId, filterDto);

        return ResponseEntity.ok(albumService.getAlbumsForUserByFilter(userId, filterDto));
    }

    @GetMapping
    @Operation(summary = "Get all albums filtered by title, description and month.")
    public ResponseEntity<List<AlbumDto>> getAllAlbumsWithFilters(
            @RequestParam(required = false) String titlePattern,
            @RequestParam(required = false) String descriptionPattern,
            @RequestParam(required = false) String month) {

        AlbumFilterDto filterDto = AlbumFilterDto.builder()
                .titlePattern(titlePattern)
                .descriptionPattern(descriptionPattern)
                .month(Month.valueOf(month))
                .build();
        log.info("Fetching all albums with filters {}", filterDto);

        return ResponseEntity.ok(albumService.getAllAlbumsByFilter(filterDto));
    }

    @GetMapping("users/{userId}/favorites")
    @Operation(summary = "Get user's favorite albums filtered by title, description and month.")
    public ResponseEntity<List<AlbumDto>> getUserFavoriteAlbumsWithFilters(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.") long userId,
            @RequestParam(required = false) String titlePattern,
            @RequestParam(required = false) String descriptionPattern,
            @RequestParam(required = false) String month) {

        AlbumFilterDto filterDto = AlbumFilterDto.builder()
                .titlePattern(titlePattern)
                .descriptionPattern(descriptionPattern)
                .month(Month.valueOf(month))
                .build();
        log.info("Fetching all user's #{} favorite albums with filters {}.", userId, filterDto);

        return ResponseEntity.ok(albumService.getFavoriteAlbumsForUserByFilter(userId, filterDto));
    }

    @PutMapping
    @Operation(summary = "Update album.")
    public ResponseEntity<AlbumDto> updateAlbum(@Valid @RequestBody AlbumUpdateDto albumDto) {
        log.info("Updating album with id #{}", albumDto.getId());

        return ResponseEntity.ok(albumService.updateAlbum(albumDto));
    }

    @DeleteMapping("/{albumId}/users/{userId}")
    @Operation(summary = "Delete album.")
    public ResponseEntity<Void> deleteAlbum(
            @PathVariable @Positive(message = "AlbumId must be greater than 0.")
            @NotNull(message = "AlbumId must not be null.") long albumId,
            @PathVariable @Positive(message = "UserId must be greater than 0.")
            @NotNull(message = "UserId must not be null.") long userId) {
        log.info("Deleting album. UserId #{}, AlbumId #{}", userId, albumId);

        albumService.deleteAlbum(userId, albumId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
