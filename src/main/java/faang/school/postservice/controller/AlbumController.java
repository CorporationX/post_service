package faang.school.postservice.controller;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.FavoriteAlbumDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.PostAlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.service.AlbumService;
import faang.school.postservice.service.FavoriteAlbumService;
import faang.school.postservice.service.PostAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;
    private final PostAlbumService postAlbumService;
    private final FavoriteAlbumService favoriteAlbumService;

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody AlbumDto albumDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(albumService.create(albumDto));
    }

    @PostMapping("/posts")
    public ResponseEntity<PostAlbumDto> addPostToAlbum(@RequestBody PostAlbumDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postAlbumService.addPostToAlbum(dto));
    }

    @PostMapping("/favorite")
    public ResponseEntity<FavoriteAlbumDto> addFavoriteAlbum(@RequestBody FavoriteAlbumDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteAlbumService.addAlbumToFavorite(dto));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Album>> getAlbums(@RequestBody AlbumFilterDto dto) {
        return ResponseEntity.ok(albumService.findWithFilter(dto));
    }
}