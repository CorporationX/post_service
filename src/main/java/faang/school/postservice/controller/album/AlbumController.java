package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.CreateAlbumDto;
import faang.school.postservice.dto.album.UpdateAlbumDto;
import faang.school.postservice.dto.album.UpdateAlbumVisibilityDto;
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

    @PostMapping
    public AlbumResponseDto createAlbum(@RequestHeader(value = "x-user-id") long authorId,
                                        @RequestBody CreateAlbumDto albumDto) {
        Album album = albumMapper.toEntity(albumDto);
        Album createdAlbum = albumService.createAlbum(authorId, album, albumDto.getChosenUserIds());
        return albumMapper.toAlbumResponseDto(createdAlbum);
    }

    @GetMapping("/{albumId}")
    public AlbumResponseDto getAlbum(@RequestHeader(value = "x-user-id") long userId,
                                     @PathVariable long albumId) {
        Album album = albumService.getAlbum(userId, albumId);
        return albumMapper.toAlbumResponseDto(album);
    }

    @PutMapping("/update")
    public AlbumResponseDto updateAlbum(@RequestHeader(value = "x-user-id") long userId,
                                        @RequestBody UpdateAlbumDto albumDto) {
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
    public AlbumResponseDto deleteAlbum(@RequestHeader(value = "x-user-id") long userId,
                                        @PathVariable long albumId) {
        Album album = albumService.deleteAlbum(userId, albumId);
        return albumMapper.toAlbumResponseDto(album);
    }

    @PutMapping("/add-to-favorites/{albumId}")
    public AlbumResponseDto addAlbumToFavorites(@RequestHeader(value = "x-user-id") long userId,
                                                @PathVariable long albumId) {
        Album album = albumService.addAlbumToFavorites(userId, albumId);
        return albumMapper.toAlbumResponseDto(album);
    }

    @DeleteMapping("/delete-from-favorites/{albumId}")
    public AlbumResponseDto deleteAlbumFromFavorites(@RequestHeader(value = "x-user-id") long userId,
                                                     @PathVariable long albumId) {
        Album album = albumService.deleteAlbumFromFavorites(userId, albumId);
        return albumMapper.toAlbumResponseDto(album);
    }

    @PutMapping("/add-new-posts/{albumId}")
    public AlbumResponseDto addNewPosts(@RequestHeader(value = "x-user-id") long userId,
                                        @PathVariable long albumId,
                                        @RequestBody List<Long> postIds) {
        Album album = albumService.addNewPosts(userId, albumId, postIds);
        return albumMapper.toAlbumResponseDto(album);
    }

    @PutMapping("/delete-posts/{albumId}")
    public AlbumResponseDto deletePosts(@RequestHeader(value = "x-user-id") long userId,
                                        @PathVariable long albumId,
                                        @RequestBody List<Long> postIds) {
        Album album = albumService.deletePosts(userId, albumId, postIds);
        return albumMapper.toAlbumResponseDto(album);
    }

    @PostMapping("/get-user-albums")
    public List<AlbumResponseDto> getUserAlbums(@RequestHeader(value = "x-user-id") long userId,
                                                @RequestBody AlbumFilterDto filters) {
        List<Album> foundAlbums = albumService.getUserAlbums(userId, filters);
        return albumMapper.toAlbumResponseDtos(foundAlbums);
    }

    @PostMapping("/get-all-albums")
    public List<AlbumResponseDto> getAllAlbums(@RequestHeader(value = "x-user-id") long userId,
                                               @RequestBody AlbumFilterDto filters) {
        List<Album> foundAlbums = albumService.getAllAlbums(userId, filters);
        return albumMapper.toAlbumResponseDtos(foundAlbums);
    }

    @PostMapping("/get-favorite-albums")
    public List<AlbumResponseDto> getFavoriteAlbums(@RequestHeader(value = "x-user-id") long userId,
                                                    @RequestBody AlbumFilterDto filters) {
        List<Album> foundAlbums = albumService.getFavoriteAlbums(userId, filters);
        return albumMapper.toAlbumResponseDtos(foundAlbums);
    }
}
