package faang.school.postservice.controller.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.CreateAlbumDto;
import faang.school.postservice.dto.album.UpdateAlbumDto;
import faang.school.postservice.dto.album.UpdateAlbumVisibilityDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
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
    private final UserContext userContext;

    @PostMapping
    public AlbumResponseDto createAlbum(@Valid @RequestBody CreateAlbumDto albumDto) {
        long authorId = userContext.getUserId();
        Album album = albumMapper.toEntity(albumDto);
        Album createdAlbum = albumService.createNewAlbum(authorId, album, albumDto.getChosenUserIds());
        return albumMapper.toAlbumResponseDto(createdAlbum);
    }

    @GetMapping("/{albumId}")
    public AlbumResponseDto getAlbum(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        Album album = albumService.getAlbum(userId, albumId);
        return albumMapper.toAlbumResponseDto(album);
    }

    @PutMapping("/update")
    public AlbumResponseDto updateAlbum(@Valid @RequestBody UpdateAlbumDto albumDto) {
        long userId = userContext.getUserId();
        Album updatedAlbum = albumService.updateAlbum(userId,
                albumDto.getId(),
                albumDto.getTitle(),
                albumDto.getDescription());
        return albumMapper.toAlbumResponseDto(updatedAlbum);
    }

    @PutMapping("/update-visibility")
    public AlbumResponseDto updateAlbumVisibility(@RequestHeader(value = "x-user-id") long userId,
                                                  @RequestBody UpdateAlbumVisibilityDto albumDto) {
        Album updatedAlbum = albumService.updateAlbumVisibility(
                userId,
                albumDto.getId(),
                albumDto.getVisibility(),
                albumDto.getChosenUserIds());
        return albumMapper.toAlbumResponseDto(updatedAlbum);
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.deleteAlbum(userId, albumId);
    }

    @PutMapping("/add-to-favorites/{albumId}")
    public AlbumResponseDto addAlbumToFavorites(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        Album album = albumService.addAlbumToFavorites(userId, albumId);
        return albumMapper.toAlbumResponseDto(album);
    }

    @DeleteMapping("/delete-from-favorites/{albumId}")
    public void deleteAlbumFromFavorites(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.deleteAlbumFromFavorites(userId, albumId);
    }

    @PutMapping("/add-new-posts/{albumId}")
    public AlbumResponseDto addNewPosts(@PathVariable long albumId, @RequestBody List<Long> postIds) {
        long userId = userContext.getUserId();
        Album album = albumService.addNewPosts(userId, albumId, postIds);
        return albumMapper.toAlbumResponseDto(album);
    }

    @PutMapping("/delete-posts/{albumId}")
    public AlbumResponseDto deletePosts(@PathVariable long albumId, @RequestBody List<Long> postIds) {
        long userId = userContext.getUserId();
        Album album = albumService.deletePosts(userId, albumId, postIds);
        return albumMapper.toAlbumResponseDto(album);
    }

    @PostMapping("/get-user-albums")
    public List<AlbumResponseDto> getUserAlbums(@RequestBody AlbumFilterDto filters) {
        long userId = userContext.getUserId();
        List<Album> albums = albumService.getUserAlbums(userId, filters);
        return albumMapper.toAlbumResponseDtos(albums);
    }

    @PostMapping("/get-all-albums")
    public List<AlbumResponseDto> getAllAlbums(@RequestBody AlbumFilterDto filters) {
        long userId = userContext.getUserId();
        List<Album> albums = albumService.getAllAlbums(userId, filters);
        return albumMapper.toAlbumResponseDtos(albums);
    }

    @PostMapping("/get-favorite-albums")
    public List<AlbumResponseDto> getFavoriteAlbums(@RequestBody AlbumFilterDto filters) {
        long userId = userContext.getUserId();
        List<Album> albums = albumService.getFavoriteAlbums(userId, filters);
        return albumMapper.toAlbumResponseDtos(albums);
    }
}
