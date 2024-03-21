package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.validator.album.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {

    private final AlbumService albumService;

    private final AlbumValidator albumValidator;

    @PostMapping("/create")
    public AlbumDto createAlbum(@RequestBody AlbumDto albumDto) {
        albumValidator.validate(albumDto.getTitle(), albumDto.getDescription(), albumDto.getAuthorId());
        albumValidator.validate(albumDto.getAuthorId());
        return albumService.createAlbum(albumDto);
    }

    @PostMapping("/addPost/{albumId}")
    public void addPost(@PathVariable("albumId") long albumId, @RequestBody Post post) {
        albumService.addPost(albumId, post);
    }

    @DeleteMapping("/removePost/{albumId}/{postId}")
    public void removePost(@PathVariable("albumId") long albumId, @PathVariable("postId") long postId) {
        albumService.removePost(albumId, postId);
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@PathVariable("albumId") long albumId) {
        return albumService.getAlbum(albumId);
    }

    @PutMapping("/update/{albumId}")
    public AlbumDto updateAlbum(@PathVariable("albumId") long albumId, @RequestBody AlbumUpdateDto albumUpdateDto) {
        albumValidator.validate(albumUpdateDto.getTitle(), albumUpdateDto.getDescription(), albumUpdateDto.getAuthorId());
        albumValidator.validate(albumUpdateDto.getAuthorId());
        return albumService.updateAlbum(albumId, albumUpdateDto);
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@PathVariable("albumId") long albumId) {
        albumService.deleteAlbum(albumId);
    }

    @GetMapping("/albumsByFilte")
    public List<AlbumDto> getAlbumsByFilter(@RequestBody AlbumFilterDto albumFilterDto) {
        return albumService.getAlbumsByFilter(albumFilterDto);
    }

    @PostMapping("/addAlbumToFavorites/{albumId}/{userId}")
    public void addAlbumToFavorites(@PathVariable("albumId") long albumId, @PathVariable("userId") long userId) {
        albumValidator.validate(userId);
        albumService.addAlbumToFavorites(albumId, userId);
    }

    @DeleteMapping("/deleteAlbumFromFavorites/{albumId}/{userId}")
    public void deleteAlbumFromFavorites(long albumId, long userId) {
        albumService.deleteAlbumFromFavorites(albumId, userId);
    }


}
