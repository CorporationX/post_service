package faang.school.postservice.controller;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/new")
    public AlbumDto createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        return albumService.createAlbum(albumDto);
    }


}
