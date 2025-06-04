package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;

import java.util.List;

public interface AlbumService {

    AlbumDto createAlbum(AlbumDto albumDto);

    void addPostInAlbum(long albumId, long postId, long userId);

    void deletePostFromAlbum(long albumId, long postId, long userId);

    void addAlbumToFavorite(long albumId, long userId);

    void deleteAlbumFromFavorite(long albumId, long userId);

    AlbumDto getAlbumById(long albumId);

    List<AlbumDto> getAllUserAlbums(long userId, AlbumFilterDto albumFilterDto);

    List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto);

    List<AlbumDto> getAllFavoriteAlbums(long userId, AlbumFilterDto albumFilterDto);

    AlbumDto updateAlbum(AlbumDto albumDto);

    void deleteAlbum(long albumId, long userId);
}