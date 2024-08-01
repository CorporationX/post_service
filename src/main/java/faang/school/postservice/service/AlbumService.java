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

import java.util.List;
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
            return new AlbumLightDto();
        }
    }

    public void deleteAlbum(Long albumId) {
        Optional<Album> album = albumRepository.findById(albumId);
        if (!album.isEmpty()) {
            Long authorId = album.get().getAuthorId();
            if (authorId.equals(userContext.getUserId())) {
                albumRepository.delete(album.get());
            } else {
                log.error("author album in not contain current user");
            }
        } else {
            log.error("album is not exist");
        }
    }

    public void updateAlbum(AlbumLightDto albumLightDto) {
        Album albumEntity = albumMapper.toEntityLight(albumLightDto);

        Optional<Album> album = albumRepository.findById(albumEntity.getId());
        if (!album.isEmpty()) {
            Long authorId = album.get().getAuthorId();
            if (authorId.equals(userContext.getUserId())) {
                albumRepository.save(albumEntity);
            } else {
                log.error("author album in not contain current user");
            }
        } else {
           log.error("album is null");
        }
    }

    public void addPostForAlbum(Long albumId, Long postId) {
        if (albumRepository.findByIdWithPosts(albumId).isPresent()) {
            Optional<Album> album = albumRepository.findById(albumId);
            Long authorId = album.get().getAuthorId();

            if (authorId.equals(userContext.getUserId())) {
                Optional<Post> post = postRepository.findById(postId);
                if (album.isPresent() && post.isPresent()) {
                    album.get().getPosts().add(post.get());
                    albumRepository.save(album.get());
                }
            } else {
                log.error("authorId not contains current user");
            }
        } else {
            log.error("posts not found");
            throw new IllegalArgumentException("filter is null");
        }
    }

    public void deletePostForAlbum(Long albumId, Long postId) {
        Optional<Album> album = albumRepository.findById(albumId);
        if (album.isPresent()) {
            Long aothorId = album.get().getAuthorId();
            if (aothorId.equals(userContext.getUserId())) {
                album.get().getPosts().remove(postRepository.findById(postId));
                albumRepository.save(album.get());
            }
        } else {
            log.error("album not found");
            throw new IllegalArgumentException("album is null");
        }
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
            if (userId.equals(album.get().getAuthorId())) {
                return albumMapper.toDto(album.get());
            } else if (album.get().getVisibilityType().equals(VisibilityType.All_USER)) {
                return albumMapper.toDto(album.get());
            } else if (album.get().getVisibilityType().equals(VisibilityType.ONLY_SUBSCRIBERS)) {
                UserDto authorDto = userServiceClient.getUser(album.get().getAuthorId());
                Long authorId = authorDto.getFollowersId()
                        .stream()
                        .filter(follower -> follower.equals(userId))
                        .findFirst().orElse(null);
                if (authorId != null) {
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
                }
            }
        }
        return new AlbumDto();
    }

    public List<AlbumDto> getAlbumForFilter(AlbumFilterDto albumFilterDto) {
        if (albumFilterDto == null) {
            log.error("filter is null");
            throw new IllegalArgumentException("filter is null");
        }

        Iterable<Album> albums = albumRepository.findAll();
        Stream<Album> albumStream = StreamSupport.stream(albums.spliterator(), false);

        return albumsFilter.stream()
                .filter(albumFilter -> albumFilter.isApplicable(albumFilterDto))
                .reduce(albumStream, (cumulativeStream, albumsFilter) ->
                        albumsFilter.apply(cumulativeStream, albumFilterDto), Stream::concat)
                .map(albumMapper::toDto)
                .toList();
    }
}