package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;

import java.util.List;

public interface AlbumService {
    void createAlbum(AlbumDto albumDto);

    void updateAlbum(Long id, AlbumDto albumDto);

    void addPostToAlbum(Long id, Long postId);

    void deletePostFromAlbum(Long id, Long postId);

    void addAlbumToFavorites(Long id, Long userId);

    void deleteAlbumFromFavorites(Long id, Long userId);

    AlbumDto getAlbumById(Long id, Long userId);

    List<AlbumDto> getAlbums(Long authorId, AlbumFilterDto albumFilterDto);

    List<AlbumDto> getFavoriteAlbums(Long userId, AlbumFilterDto albumFilterDto);

    List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto, Long userId);

    void deleteAlbum(Long id);
}
