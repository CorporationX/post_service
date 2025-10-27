package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.FavoriteAlbumsDto;
import faang.school.postservice.filters.AlbumFilterDto;

import java.util.List;

public interface AlbumService {

    AlbumDto createAlbum(AlbumDto albumDto, Long userId);

    AlbumDto updateAlbum(Long albumId, AlbumDto albumDto, Long userId);

    void deleteAlbum(Long albumId, Long userId);

    AlbumDto getById(Long albumId);

    List<AlbumDto> getAllAlbums();

    List<AlbumDto> getAlbums(AlbumFilterDto albumFilterDto);

    List<AlbumDto> getAuthorsAlbums(Long authorId, AlbumFilterDto albumFilterDto);

    AlbumDto addPost(Long postId, Long albumId, Long userId);

    AlbumDto removePost(Long postId, Long albumId, Long userId);

    FavoriteAlbumsDto addToFavoriteAlbums(Long albumId, Long userId);

    void removeFromFavoriteAlbum(Long albumId, Long userId);
}
