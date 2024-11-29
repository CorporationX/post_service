package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

public interface AlbumService {

  List<Album> getAlbumsByIds(List<Long> albumsIds);

  AlbumDto add(@Valid AlbumCreateDto albumDto, Long userId);

  AlbumDto getAlbumById(Long id);

  void addPost(long albumId, long postId, long userId);

  void removePost(long albumId, long postId, long userId);

  Album findAlbumById(Long id);

  Post findPostById(Long id);

  void addAlbumToFavorites(long albumId, long userId);

  void removeAlbumFromFavorites(long albumId, long userId);

  List<AlbumDto> getUserAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto);

  List<AlbumDto> getUserFavoriteAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto);

  List<AlbumDto> getAllAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto);

  Stream<Album> getFavoriteAlbumsByUserId(Long id);

  AlbumDto update(@Valid long userId, @Valid AlbumDto albumDto);

  void remove(long userId, AlbumDto albumDto);
}
