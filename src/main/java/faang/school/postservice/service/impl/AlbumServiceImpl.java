package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreatedEvent;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.filter.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.AlbumCreatedEventPublisher;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AlbumService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final List<AlbumFilter> filters;
    private final AlbumCreatedEventPublisher albumCreatedEventPublisher;

    @Override
    public void createAlbum(AlbumDto albumDto) {
        validateAlbumAuthor(albumDto.getAuthorId());
        validateAlbumTitle(albumDto);

        Album newAlbum = albumMapper.toEntity(albumDto);

        addPostsToAlbum(albumDto, newAlbum);

        albumRepository.save(newAlbum);
        publishCreateEvent(albumDto);
    }

    private void publishCreateEvent(AlbumDto albumDto) {
        AlbumCreatedEvent event = AlbumCreatedEvent.builder()
                .albumId(albumDto.getId())
                .userId(albumDto.getAuthorId())
                .albumName(albumDto.getTitle())
                .eventTime(LocalDateTime.now())
                .build();
        albumCreatedEventPublisher.publish(event);
    }

    @Override
    public void updateAlbum(Long id, AlbumDto albumDto) {
        validateAlbumTitle(albumDto);

        Album albumToUpdate = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        albumMapper.updateFromDto(albumDto, albumToUpdate);
        addPostsToAlbum(albumDto, albumToUpdate);

        albumRepository.save(albumToUpdate);
    }

    @Override
    public void addPostToAlbum(Long id, Long postId) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        album.addPost(post);
        albumRepository.save(album);
    }

    @Override
    public void deletePostFromAlbum(Long id, Long postId) {
        albumRepository.findById(id)
                .ifPresent(album -> {
                    album.removePost(postId);
                    albumRepository.save(album);
                });
    }

    @Override
    public void addAlbumToFavorites(Long id, Long userId) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        if (!isAlbumVisibleForUser(album, userId)) {
            throw new DataValidationException("Album is not visible for user");
        }
      
        albumRepository.addAlbumToFavorites(id, userId);
    }

    @Override
    public void deleteAlbumFromFavorites(Long id, Long userId) {
        albumRepository.deleteAlbumFromFavorites(id, userId);
    }

    @Override
    public AlbumDto getAlbumById(Long id, Long userId) {
        Album album = albumRepository.findByIdWithPosts(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        if (!isAlbumVisibleForUser(album, userId)) {
            throw new DataValidationException("Album is not visible for user");
        }

        AlbumDto albumDto = albumMapper.toDto(album);
        List<Long> postIds = album.getPosts().stream()
          .map(Post::getId)
          .toList();
        albumDto.setPostIds(postIds);
  
        return albumDto;
    }

    @Override
    public List<AlbumDto> getAlbums(Long authorId, AlbumFilterDto albumFilterDto) {
        Stream<Album> albums = albumRepository.findByAuthorId(authorId);
        return albumMapper.toDto(filterAlbums(albumFilterDto, albums));
    }

    @Override
    public List<AlbumDto> getFavoriteAlbums(Long userId, AlbumFilterDto albumFilterDto) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        return albumMapper.toDto(filterAlbums(albumFilterDto, albums));
    }

    @Override
    public List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto, Long userId) {
        Stream<Album> albums = albumRepository.findAll().stream()
                .filter(album -> isAlbumVisibleForUser(album, userId));
        return albumMapper.toDto(filterAlbums(albumFilterDto, albums));
    }

    @Override
    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }

    private List<Album> filterAlbums(AlbumFilterDto albumFilterDto, Stream<Album> albums) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .flatMap(filter -> filter.apply(albumFilterDto, albums))
                .toList();
    }

    private void addPostsToAlbum(AlbumDto albumDto, Album album) {
        if (albumDto.getPostIds() != null) {
            for (Long postId : albumDto.getPostIds()) {
                Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
                album.addPost(post);
            }
        }
    }

    private void validateAlbumAuthor(Long authorId) {
        if (!userServiceClient.existsUserById(authorId)) {
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateAlbumTitle(AlbumDto albumDto) {
        if (albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())) {
            throw new EntityExistsException("Album with title " + albumDto.getTitle() + " already exists");
        }
    }

    private boolean isAlbumVisibleForUser(Album album, Long userId) {
        AlbumVisibility visibility = album.getVisibility();

        if (visibility.equals(AlbumVisibility.AUTHOR_ONLY)) {
            return userId.equals(album.getAuthorId());
        }
        if (visibility.equals(AlbumVisibility.SELECTED_USERS)) {
            return albumRepository.findSelectedUserIdsForAlbum(album.getId()).contains(userId);
        }
        if (visibility.equals(AlbumVisibility.ALL_FOLLOWERS)) {
            Set<Long> followerIds = userServiceClient.getFollowers(
                            album.getAuthorId(), new UserFilterDto()
                    ).stream()
                    .map(UserDto::getId)
                    .collect(Collectors.toSet());
            return followerIds.contains(userId);
        }

        return true;
    }
}
