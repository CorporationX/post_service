package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.CreateAlbumDto;
import faang.school.postservice.dto.album.UpdateAlbumDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;
    private final AlbumMapper albumMapper;

    @PostMapping("/create")
    public AlbumDto createNewAlbum(@RequestHeader(value = "x-user-id") long authorId,
                                   @RequestBody CreateAlbumDto albumDto) {
        Album album = albumMapper.toEntity(albumDto);
        Album createdAlbum = albumService.createNewAlbum(authorId, album);
        return albumMapper.toDto(createdAlbum);
    }

    @GetMapping("/{albumId}")
    public AlbumDto getAlbum(@RequestHeader(value = "x-user-id") long userId,
                             @PathVariable long albumId) {
        Album album = albumService.getAlbum(userId, albumId);
        return albumMapper.toDto(album);
    }

    @PutMapping("/update")
    public AlbumDto updateAlbum(@RequestHeader(value = "x-user-id") long userId,
                                @RequestBody UpdateAlbumDto albumDto) {
        Album updatedAlbum = albumService.updateAlbum(userId,
                albumDto.getId(),
                albumDto.getTitle(),
                albumDto.getDescription());
        return albumMapper.toDto(updatedAlbum);
    }

    @DeleteMapping("/{albumId}")
    public AlbumDto deleteAlbum(@RequestHeader(value = "x-user-id") long userId,
                                @PathVariable long albumId) {
        Album album = albumService.deleteAlbum(userId, albumId);
        return albumMapper.toDto(album);
    }

    @PutMapping("/add-to-favorites/{albumId}")
    public AlbumDto addAlbumToFavorites(@RequestHeader(value = "x-user-id") long userId,
                                        @PathVariable long albumId) {
        Album album = albumService.addAlbumToFavorites(userId, albumId);
        return albumMapper.toDto(album);
    }

    @DeleteMapping("/delete-from-favorites/{albumId}")
    public AlbumDto deleteAlbumFromFavorites(@RequestHeader(value = "x-user-id") long userId,
                                             @PathVariable long albumId) {
        Album album = albumService.deleteAlbumFromFavorites(userId, albumId);
        return albumMapper.toDto(album);
    }

    @PutMapping("/add-new-posts/{albumId}")
    public AlbumDto addNewPosts(@RequestHeader(value = "x-user-id") long userId,
                                @PathVariable long albumId,
                                @RequestBody List<Long> postIds) {
        Album album = albumService.addNewPosts(userId, albumId, postIds);
        return albumMapper.toDto(album);
    }

    @PutMapping("/delete-posts/{albumId}")
    public AlbumDto deletePosts(@RequestHeader(value = "x-user-id") long userId,
                                @PathVariable long albumId,
                                @RequestBody List<Long> postIds) {
        Album album = albumService.deletePosts(userId, albumId, postIds);
        return albumMapper.toDto(album);
    }

    @PostMapping("/get-user-albums")
    public List<Album> getUserAlbums(@RequestHeader(value = "x-user-id") long userId,
                                     @RequestBody AlbumFilterDto filters) {
        return albumService.getUserAlbums(userId, filters);
    }

    @PostMapping("/get-all-albums")
    public List<Album> getAllAlbums(@RequestHeader(value = "x-user-id") long userId,
                                    @RequestBody AlbumFilterDto filters) {
        return albumService.getAllAlbums(userId, filters);
    }

    @PostMapping("/get-favorite-albums")
    public List<Album> getFavoriteAlbums(@RequestHeader(value = "x-user-id") long userId,
                                         @RequestBody AlbumFilterDto filters) {
        return albumService.getFavoriteAlbums(userId, filters);
    }
}
