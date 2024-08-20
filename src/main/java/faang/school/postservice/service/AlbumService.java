package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumLightDto;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.UserVisibility;
import faang.school.postservice.model.VisibilityType;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final List<AlbumFilter> albumsFilter;

    public AlbumLightDto createAlbum(AlbumLightDto albumLightDto) {
        Album album = albumMapper.toEntityLight(albumLightDto);
        if (!albumRepository.existsByTitleAndAuthorId(album.getTitle(), userContext.getUserId())) {
            return albumMapper.toDtoLight(albumRepository.save(album));
        } else {
            log.error("album is not exist for title and userId");
            throw new IllegalArgumentException("album is not exist for title and userId");
        }
    }

    public AlbumDto deleteAlbum(Long albumId) {
        Optional<Album> album = albumRepository.findById(albumId);
        if (!album.isEmpty()) {
            Long authorId = album.get().getAuthorId();
            if (authorId.equals(userContext.getUserId())) {
                albumRepository.delete(album.get());
                return albumMapper.toDto(album.get());
            } else {
                log.error("author album in not contain current user");
            }
        } else {
            log.error("album is not exist");
            throw new IllegalArgumentException("album is not exist");
        }
        return albumMapper.toDto(album.get());
    }

    public AlbumLightDto updateAlbum(AlbumLightDto albumLightDto) {
        Album albumEntity = albumMapper.toEntityLight(albumLightDto);

        Optional<Album> album = albumRepository.findById(albumEntity.getId());
        if (!album.isEmpty()) {
            Long authorId = album.get().getAuthorId();
            if (authorId.equals(userContext.getUserId())) {
                albumRepository.save(albumEntity);
                return albumMapper.toDtoLight(albumEntity);
            } else {
                log.error("you can't update the album");
                throw new SecurityException("you can't update the album");
            }
        } else {
            log.error("album is null");
            throw new NoSuchElementException("album is null");
        }
    }

    public AlbumLightDto addPostForAlbum(Long albumId, Long postId) {
        if (albumRepository.findByAlbumIdAndPostId(postId, albumId).isEmpty()) {
            Optional<Album> album = albumRepository.findById(albumId);
            Long authorId = album.get().getAuthorId();

            if (authorId.equals(userContext.getUserId())) {
                Optional<Post> post = postRepository.findById(postId);
                if (album.isPresent() && post.isPresent()) {
                    album.get().getPosts().add(post.get());
                    albumRepository.save(album.get());
                    return albumMapper.toDtoLight(album.get());
                }
            } else {
                log.error("authorId not contains current user");
            }
        } else {
            log.error("posts for album" + albumId + "not found");
            throw new IllegalArgumentException("posts not found");
        }
        return albumMapper.toDtoLight(new Album());
    }

    public AlbumLightDto deletePostForAlbum(Long albumId, Long postId) {
        Optional<Album> album = albumRepository.findById(albumId);
        if (album.isPresent()) {
            Long authorId = album.get().getAuthorId();
            if (authorId.equals(userContext.getUserId())) {
                album.get().getPosts().remove(postRepository.findById(postId));
                albumRepository.save(album.get());
                return albumMapper.toDtoLight(album.get());
            }
        } else {
            log.error("album not found");
            throw new NoSuchElementException("album is null");
        }
        return albumMapper.toDtoLight(album.get());
    }

    public void addAlbumFavorite(Long albumId) {
        albumRepository.addAlbumToFavorites(albumId, userContext.getUserId());
    }

    public void deleteAlbumFavorite(Long albumId) {
        albumRepository.deleteAlbumFromFavorites(albumId, userContext.getUserId());
    }

    public AlbumDto getAlbum(Long albumId) {
        Long userId = userContext.getUserId();
        Optional<Album> album = albumRepository.findByIdWithPosts(albumId);
        if (userId == null || !album.isPresent()) {
            return new AlbumDto();
        } else {
            return isUserVisibilityType(userId, album);
        }
    }

    public List<AlbumDto> getAlbumForFilter(AlbumFilterDto albumFilterDto) {
        Long userId = userContext.getUserId();
        if (albumFilterDto == null) {
            log.error("filter is null");
            throw new IllegalArgumentException("filter is null");
        }
        Iterable<Album> albums = albumRepository.findAll();
        Stream<Album> albumStream = StreamSupport.stream(albums.spliterator(), false);
        albumStream.map(album -> isUserVisibilityType(userId, Optional.ofNullable(album)));

        return albumsFilter.stream()
                .filter(albumFilter -> albumFilter.isApplicable(albumFilterDto))
                .reduce(albumStream, (cumulativeStream, albumsFilter) ->
                        albumsFilter.apply(cumulativeStream, albumFilterDto), Stream::concat)
                .map(albumMapper::toDto)
                .toList();
    }

    private AlbumDto isUserVisibilityType(Long userId, Optional<Album> album) {
        if (userId.equals(album.get().getAuthorId())) {
            return albumMapper.toDto(album.get());
        } else if (album.get().getVisibilityType().equals(VisibilityType.All_USER)) {
            return albumMapper.toDto(album.get());
        } else if (album.get().getVisibilityType().equals(VisibilityType.ONLY_SUBSCRIBERS)) {
            UserDto authorDto = userServiceClient.getUser(album.get().getAuthorId());
            if (!authorDto.getFollowersId().contains(userId)) {
                return albumMapper.toDto(album.get());
            } else {
                log.info("user is not follower");
                return new AlbumDto();
            }
        } else if (album.get().getVisibilityType().equals(VisibilityType.ONLY_USERS_SELECTED)) {
            UserVisibility userVisibilityResult = album.get().getVisibilityUsers()
                    .stream()
                    .filter(userVisibility -> userVisibility.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);
            if (userVisibilityResult != null) {
                return albumMapper.toDto(album.get());
            } else {
                log.info("user is not follower");
                return new AlbumDto();
            }
        } else if (album.get().getVisibilityType().equals(VisibilityType.ONLY_AUTHOR)) {
            return new AlbumDto();
        } else {
            log.error("album not found");
            throw new NoSuchElementException("album is null");
        }
    }
}