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
    @PutMapping("/createAlbum")
    public AlbumLightDto createAlbum(AlbumLightDto albumLightDto) {
        if (albumLightDto == null) {
            log.error("albumLightDto is null");
            throw new IllegalArgumentException("albumLightDto is null");
        } else {
            return albumService.createAlbum(albumLightDto);
        }
    }

    @Operation(summary = "Добавление поста к альбому",
            description = "Добавляет пост к альбому по id альбома и id поста")
    @PutMapping("/addPostForAlbum")
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
    @DeleteMapping("/deletePost")
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