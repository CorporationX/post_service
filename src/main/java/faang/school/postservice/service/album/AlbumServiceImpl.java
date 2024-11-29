package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.album.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AlbumServiceImpl implements AlbumService {

  private final AlbumRepository albumRepository;
  private final PostRepository postRepository;
  private final UserServiceClient userServiceClient;
  private final AlbumMapper albumMapper;
  private final List<AlbumFilter> albumFilters;

  @Override
  public List<Album> getAlbumsByIds(List<Long> albumsIds) {
    if (albumsIds == null) {
      return null;
    }
    return albumRepository.findAllById(albumsIds);
  }

  @Override
  public AlbumDto add(AlbumCreateDto albumDto, Long userId) {
    validateAlbumAuthorTitle(albumDto, userId);
    validateUser(userId);
    Album album = albumMapper.toEntityFromCreateDto(albumDto);
    album.setPosts(new ArrayList<>());
    album = albumRepository.save(album);
    log.info("New album with ID {} was created", album.getId());
    return albumMapper.toDto(album);
  }

  @Override
  public AlbumDto getAlbumById(Long id) {
    Album album = findAlbumById(id);
    return albumMapper.toDto(album);
  }

  @Override
  public void addPost(long albumId, long postId, long userId) {
    Album album = findAlbumById(albumId);
    Post post = findPostById(postId);
    validateUserAccess(album.getAuthorId(), userId);
    album.addPost(post);
    albumRepository.save(album);
    log.info("The post {} was added to the album {}", post.getContent(), album.getTitle());
  }

  @Override
  public void removePost(long albumId, long postId, long userId) {
    Album album = findAlbumById(albumId);
    Post post = findPostById(postId);
    validateUserAccess(album.getAuthorId(), userId);
    album.removePost(postId);
    albumRepository.save(album);
    log.info("The post {} was deleted from the album {}", post.getContent(), album.getTitle());
  }

  @Override
  public Album findAlbumById(Long id) {
    return albumRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Album does not exist"));
  }

  @Override
  public Post findPostById(Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Post does not exist"));
  }

  @Transactional
  @Override
  public void addAlbumToFavorites(long albumId, long userId) {
    validateUser(userId);
    String title = findAlbumById(albumId).getTitle();
    albumRepository.addAlbumToFavorites(albumId, userId);
    log.info("The album {} was added to favorites", title);
  }

  @Transactional
  @Override
  public void removeAlbumFromFavorites(long albumId, long userId) {
    Album album = findAlbumById(albumId);
    validateUser(userId);
    validateUserAccess(album.getAuthorId(), userId);
    albumRepository.deleteAlbumFromFavorites(albumId, userId);
    log.info("The album {} was removed from favorites", findAlbumById(albumId).getTitle());
  }

  public List<AlbumDto> getAlbumsWithFilter(Stream<Album> albums, Long userId,
      AlbumFilterDto albumFilterDto) {
    return albumFilters.stream()
        .filter(albumFilter -> albumFilter.isApplicable(albumFilterDto))
        .reduce(albums, (stream,
                albumFilter) -> albumFilter.apply(stream, albumFilterDto),
            (s1, s2) -> s1)
        .map(albumMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public List<AlbumDto> getUserAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto) {
    Stream<Album> albums = albumRepository.findByAuthorId(userId);
    return getAlbumsWithFilter(albums, userId, albumFilterDto);
  }

  @Transactional
  @Override
  public List<AlbumDto> getUserFavoriteAlbumsWithFilters(Long userId,
      AlbumFilterDto albumFilterDto) {
    Stream<Album> albums = getFavoriteAlbumsByUserId(userId);
    return getAlbumsWithFilter(albums, userId, albumFilterDto);
  }

  @Override
  public List<AlbumDto> getAllAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto) {
    Stream<Album> albums = albumRepository.findAll().stream();
    return getAlbumsWithFilter(albums, userId, albumFilterDto);
  }

  @Override
  public Stream<Album> getFavoriteAlbumsByUserId(Long userId) {
    validateUser(userId);
    return albumRepository.findFavoriteAlbumsByUserId(userId);
  }

  @Override
  public AlbumDto update(long userId, AlbumDto albumDto) {
    validateUserAccess(albumDto.getAuthorId(), userId);
    validateAlbumAuthorTitle(albumDto);
    Album album = findAlbumById(albumDto.getId());
    album.setTitle(albumDto.getTitle());
    album.setDescription(albumDto.getDescription());
    album.setUpdatedAt(LocalDateTime.now());
    return albumMapper.toDto(albumRepository.save(album));
  }

  @Override
  public void remove(long userId, AlbumDto albumDto) {
    validateUserAccess(albumDto.getAuthorId(), userId);
    Album album = findAlbumById(albumDto.getId());
    albumRepository.delete(album);
  }

  private void validateUserAccess(long albumAuthorId, long userId) {
    if (albumAuthorId != userId) {
      throw new DataValidationException("Only owner can add or delete post from this album");
    }
  }

  private void validateAlbumAuthorTitle(AlbumDto albumDto) {
    long authorId = albumDto.getAuthorId();
    String title = albumDto.getTitle();
    if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
      throw new DataValidationException(
          String.format("User with ID %d already has album with Title %s", authorId, title));
    }
  }

  private void validateAlbumAuthorTitle(AlbumCreateDto albumDto, Long userId) {
    String title = albumDto.getTitle();
    if (albumRepository.existsByTitleAndAuthorId(title, userId)) {
      throw new DataValidationException(
          String.format("User with ID %d already has album with Title %s", userId, title));
    }
  }


  void validateUser(long userId) {
    if (userServiceClient.getUser(userId) == null) {
      throw new EntityNotFoundException(String.format("User with ID %d not found", userId));
    }
  }

}
