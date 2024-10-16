package faang.school.postservice.service;

import faang.school.postservice.model.dto.AlbumDto;
import faang.school.postservice.model.dto.AlbumFilterDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AlbumService {
    AlbumDto create(AlbumDto albumDto);

    String addPostToAlbum(Long albumId, Long postId);

    AlbumDto deletePostFromAlbum(Long albumId, Long postId);

    String addAlbumToFavorites(Long albumId);

    String deleteAlbumFromFavorites(Long albumId);

    AlbumDto getAlbumById(Long albumId);

    List<AlbumDto> getUserAlbums(AlbumFilterDto albumFilterDto);

    List<AlbumDto> getAllUsersAlbums(AlbumFilterDto albumFilterDto);

    List<AlbumDto> getUserFavoriteAlbums(AlbumFilterDto albumFilterDto);

    AlbumDto update(Long albumId, AlbumDto albumDto);

    void delete(Long albumId);
}
