package faang.school.postservice.service;

import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.dto.albums.AlbumFilterDto;

import java.util.List;

public interface AlbumService {

    AlbumDto createAlbum(AlbumDto albumDto);

    AlbumDto addPostToAlbum(long albumId, long postId, long userId);

    AlbumDto removePostFromAlbum(long albumId, long postId, long userId);

    AlbumDto addAlbumToFavorite(long albumId, long userId);

    AlbumDto removeAlbumFromFavorite(long albumId, long userId);

    AlbumDto getAlbumById(long albumId);

    List<AlbumDto> getAllUserAlbums(long userId, AlbumFilterDto albumFilterDto);

//    List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto);

//    List<AlbumDto> getAllUserFavoriteAlbums(long userId, AlbumFilterDto albumFilterDto);
//
//    AlbumDto updateAlbum(long albumId, long userId, AlbumDto albumDto);
//
//    AlbumDto deleteAlbum(long albumId, long userId);
}
