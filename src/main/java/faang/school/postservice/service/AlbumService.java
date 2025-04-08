package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;

import java.util.List;

public interface AlbumService {

    AlbumResponseDto getAlbumById(long id);

    List<AlbumResponseDto> getAlbumsByAuthorId(long authorId);

    AlbumResponseDto updateAlbumVisibility(long id, AlbumVisibility albumVisibility);

    List<Long> addUsersForAccessAlbum(long id, AlbumUsersDto albumUsersDto);

    AlbumDto createAlbum(long userId, AlbumDto albumDto);

    AlbumDto addPostToAlbum(long userId, long postId, long albumId);

    void deletePostFromAlbum(long userId, long postId, long albumId);

    AlbumDto getAlbumByIdReturnAlbumDto(long albumId);

    AlbumDto updateAlbum(long userId, AlbumDto albumDto);

    void deleteAlbum(long userId, long albumId);

    List<AlbumDto> getAllAlbumsByAuthorIdWithFilters(long userId, AlbumFilterDto filters);

    List<AlbumDto> getAllAlbumsWithFilters(AlbumFilterDto filters);

    void addFavouriteAlbum(long userId, long albumId);

    void deleteFavouriteAlbum(long userId, long albumId);

    List<AlbumDto> getFavouriteAlbumsByUserId(long userId, AlbumFilterDto filters);

    List<AlbumDto> filterAlbums(List<Album> albums, AlbumFilterDto filters);

}
