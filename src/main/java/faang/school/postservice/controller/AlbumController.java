package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AlbumController", description = "Посылаем запросы в AlbumController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/albums")
public class AlbumController {
    private final AlbumService albumService;

    @Operation(
            summary = "Создаём альбом",
            description = "Получает AlbumDto и создаёт альбом"
    )
    @PostMapping
    public AlbumDto createAlbum(AlbumDto albumDto) {
        return albumService.createAlbum(albumDto);
    }
}
