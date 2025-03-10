package faang.school.postservice.controller;

import faang.school.postservice.dto.posts.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    public void createAlbum(String title, String description) {

    }

    public void addPost(long id, String title) {

    }

    public void addAlbumToFavorites() {

    }

    @GetMapping("/api/v1/albums/{id}")
    public Album getAlbum(@PathVariable long id) {
        var album = albumService.getAlbum(id);
        return album;
    }

    public List<AlbumDto> getAllHisAlbum() {
        return AlbumDto;
    }

    public List<AlbumDto> getAllGeneralAlbum() {
        return AlbumDto;
    }

    public List<Album> getAllFavoriteAlbum() {
        return AlbumDto;
    }

    public void updateAlbum() {

    }

    public void deleteAlbum() {

    }
}
