package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
@Tag(name = "Albums", description = "Endpoints for managing albums")
public class AlbumController {
    private final AlbumService albumService;

    @Operation(summary = "Create an album")
    @PostMapping
    public AlbumDto create(@Valid @RequestBody AlbumDto albumDto) {
        return albumService.create(albumDto);
    }
}
