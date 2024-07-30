package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumLightDto;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/album")
@Tag(name = "Контроллер альбома")
public class AlbumController {
    private final AlbumService albumService;

    @Operation(summary = "Создание альбома",
            description = "Создает новый пустой альбом")
    @PutMapping("/create")
    public AlbumLightDto createAlbum(AlbumLightDto albumLightDto) {
        if (albumLightDto == null) {
            log.error("albumLightDto is null");
            throw new IllegalArgumentException("albumLightDto is null");
        } else {
            return albumService.createAlbum(albumLightDto);
        }
    }

    @Operation(summary = "Удалить альбом",
            description = "Пользователь удаляет свой альбом по id")
    @PutMapping("/delete")
    public void deleteAlbum(Long albumId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            albumService.deleteAlbum(albumId);
        }
    }

    @Operation(summary = "Обновить альбом",
            description = "Пользователь обновляет свой альбом по id")
    @PutMapping("/update")
    public void updateAlbum(AlbumLightDto albumLightDto) {
        if (albumLightDto == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            albumService.updateAlbum(albumLightDto);
        }
    }

    @Operation(summary = "Добавление поста к альбому",
            description = "Добавляет пост к альбому по id альбома и id поста")
    @PutMapping("/addPost/{albumId}/{postId}")
    public void addPostForAlbum(Long albumId, Long postId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else if (postId == null) {
            log.error("postId is null");
            throw new IllegalArgumentException("postId is null");
        } else {
            albumService.addPostForAlbum(albumId, postId);
        }
    }

    @Operation(summary = "Удаление поста",
            description = "Удаляет пост из альбома по id альбома и postId")
    @DeleteMapping("/deletePost/{albumId}/{postId}")
    public void deletePostForAlbum(Long albumId, Long postId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else if (postId == null) {
            log.error("postId is null");
            throw new IllegalArgumentException("postId is null");
        } else {
            albumService.deletePostForAlbum(albumId, postId);
        }
    }

    @Operation(summary = "Добавить альбом в избранные",
            description = "Добавляет альбом в избранные по id альбома")
    public void addAlbumFavorite(Long albumId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            albumService.addAlbumFavorite(albumId);
        }
    }

    @Operation(summary = "Удалить альбом из избранных",
            description = "Добавить альбом в избранные по id альбома")
    public void deleteAlbumFavorite(Long albumId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            albumService.deleteAlbumFavorite(albumId);
        }
    }

    @Operation(summary = "Получить альбом",
            description = "Получить альбом по id")
    @GetMapping("/getAlbum/{albumId}")
    public AlbumDto getAlbum(@PathVariable Long albumId) {
        if (albumId == null) {
            log.error("albumId is null");
            throw new IllegalArgumentException("albumId is null");
        } else {
            return albumService.getAlbum(albumId);
        }
    }
}