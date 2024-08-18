package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumLightDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/album")
@Tag(name = "Контроллер альбома")
public class AlbumController {
    private final AlbumService albumService;

    @Operation(summary = "Создание альбома",
            description = "Создает новый пустой альбом")
    @PostMapping("/album")
    public AlbumLightDto createAlbum(@Valid @RequestBody AlbumLightDto albumLightDto) {
        if (albumLightDto == null) {
            log.error("albumLightDto is null");
            throw new IllegalArgumentException("albumLightDto is null");
        } else {
            return albumService.createAlbum(albumLightDto);
        }
    }

    @Operation(summary = "Удалить альбом",
            description = "Пользователь удаляет свой альбом по id")
    @DeleteMapping("/album")
    public AlbumDto deleteAlbum(@PathVariable Long albumId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            return albumService.deleteAlbum(albumId);
        }
    }

    @Operation(summary = "Обновить альбом",
            description = "Пользователь обновляет свой альбом по id")
    @PutMapping("/album")
    public AlbumLightDto updateAlbum(@Valid @RequestBody AlbumLightDto albumLightDto) {
        if (albumLightDto == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            return albumService.updateAlbum(albumLightDto);
        }
    }

    @Operation(summary = "Добавление поста к альбому",
            description = "Добавляет пост к альбому по id альбома и id поста")
    @PutMapping("/album/{albumId}/{postId}")
    public AlbumLightDto addPostForAlbum(@PathVariable Long albumId, @PathVariable Long postId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else if (postId == null) {
            log.error("postId is null");
            throw new IllegalArgumentException("postId is null");
        } else {
            return albumService.addPostForAlbum(albumId, postId);
        }
    }

    @Operation(summary = "Удаление поста",
            description = "Удаляет пост из альбома по id альбома и postId")
    @DeleteMapping("/album/{albumId}/{postId}")
    public AlbumLightDto deletePostForAlbum(@PathVariable Long albumId, @PathVariable Long postId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else if (postId == null) {
            log.error("postId is null");
            throw new IllegalArgumentException("postId is null");
        } else {
            return albumService.deletePostForAlbum(albumId, postId);
        }
    }

    @Operation(summary = "Добавить альбом в избранные",
            description = "Добавляет альбом в избранные по id альбома")
    @PutMapping("/album/favorite/{albumId}")
    public void addAlbumFavorite(@PathVariable Long albumId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            albumService.addAlbumFavorite(albumId);
        }
    }

    @Operation(summary = "Удалить альбом из избранных",
            description = "Добавить альбом в избранные по id альбома")
    @DeleteMapping("/album/favorite/{albumId}")
    public void deleteAlbumFavorite(@PathVariable  Long albumId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            albumService.deleteAlbumFavorite(albumId);
        }
    }

    @Operation(summary = "Получить альбом",
            description = "Получить альбом по id")
    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@PathVariable Long albumId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            return albumService.getAlbum(albumId);
        }
    }

    @Operation(summary = "Получить альбомы с фильтрами",
            description = "Отфильтровать список альбомов по условиям")
    @GetMapping("/filter")
    public List<AlbumDto> getAlbumForFilter(@Valid @RequestBody AlbumFilterDto albumFilterDto) {
        if (albumFilterDto == null) {
            log.error("albumFilterDto is null");
            throw new IllegalArgumentException("albumFilterDto is null");
        } else {
            return albumService.getAlbumForFilter(albumFilterDto);
        }
    }
}