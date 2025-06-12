package faang.school.postservice.service;

import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.dto.albums.AlbumFilterDto;

import java.util.List;

public interface AlbumService {

    AlbumDto createAlbum(AlbumDto albumDto);

    AlbumDto addPostToAlbum(long albumId, long postId);

    AlbumDto removePostFromAlbum(long albumId, long postId);

    AlbumDto addAlbumToFavorite(long albumId);

    AlbumDto removeAlbumFromFavorite(long albumId);

    AlbumDto getAlbumById(long albumId);

    List<AlbumDto> getAllUserAlbums(AlbumFilterDto albumFilterDto);

    List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto);

    List<AlbumDto> getAllUserFavoriteAlbums(AlbumFilterDto albumFilterDto);

    AlbumDto updateAlbum(long albumId, AlbumDto albumDto);

    AlbumDto deleteAlbum(long albumId);
}
