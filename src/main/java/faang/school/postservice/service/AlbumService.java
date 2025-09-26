package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;

import java.util.List;


public interface AlbumService {
    AlbumUpdateDto createAlbum(AlbumCreateDto albumCreateDto);

    AlbumUpdateDto updateAlbum(Long albumId, AlbumUpdateDto albumUpdateDto);

    void deleteAlbum(Long albumId, Long userId);

    AlbumUpdateDto addPostsToAlbum(Long albumId, AlbumUpdateDto albumUpdateDto);

    void deletePostsFromAlbum(Long albumId, AlbumUpdateDto albumUpdateDto);

    void addAlbumToFavorites(Long userId, Long albumId);

    void deleteAlbumFromFavorites(Long userId, Long albumId);

    AlbumUpdateDto getAlbumById(Long albumId);

    List<AlbumUpdateDto> getUserAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto);

    List<AlbumUpdateDto> getAllAlbumsWithFilters(AlbumFilterDto albumFilterDto);

    List<AlbumUpdateDto> getAllFavoritesUserAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto);
}
