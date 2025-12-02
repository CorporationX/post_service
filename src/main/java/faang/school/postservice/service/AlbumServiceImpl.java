package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.FavoriteAlbumsDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.filters.AlbumFilter;
import faang.school.postservice.filters.AlbumFilterDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.FavoriteAlbums;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.FavoriteAlbumsRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final List<AlbumFilter> albumFilters;
    private final FavoriteAlbumsRepository favoriteAlbumsRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;

    @Override
    public AlbumDto createAlbum(AlbumDto albumDto, Long userId) {

        try {
            userServiceClient.getUser(userId);
        } catch (Exception e) {
            String message = String.format("User with id %s does not exist", userId);
            log.error(Arrays.toString(e.getStackTrace()));
            throw new DataValidationException(message);
        }

        isAllowedChange(albumDto.authorId(), userId);

        if (albumRepository.existsByTitleAndAuthorId(albumDto.title(), albumDto.authorId())) {
            String message = String.format("Album by author with id %s with title %s already exists",
                    albumDto.authorId(), albumDto.title());
            log.error(message);
            throw new ForbiddenException(message);
        }

        Album album = albumMapper.toAlbum(albumDto);
        album.setCreatedAt(LocalDateTime.now());
        album = albumRepository.save(album);
        log.info("Album with id {} was created", album.getId());

        return albumMapper.toDto(album);
    }

    @Override
    public AlbumDto updateAlbum(Long albumId, AlbumDto albumDto, Long userId) {
        isAllowedChange(albumId, userId);
        validateExistAlbumById(albumId);

        Album album = albumMapper.updateAlbum(albumDto,
                albumRepository.findById(albumId).orElseThrow());
        album.setUpdatedAt(LocalDateTime.now());
        log.info("Album with id {} was updated", albumId);

        return albumMapper.toDto(albumRepository.save(album));
    }

    @Override
    public void deleteAlbum(Long albumId, Long userID) {
        isAllowedChange(albumId, userID);
        validateExistAlbumById(albumId);
        log.info("Album with id {} was deleted", albumId);

        albumRepository.deleteById(albumId);
    }

    @Override
    public AlbumDto getById(Long albumId) {
        validateExistAlbumById(albumId);

        return albumMapper.toDto(albumRepository.findById(albumId).orElseThrow());
    }

    @Override
    public List<AlbumDto> getAllAlbums() {

        return albumMapper.toDtoList((List<Album>) albumRepository.findAll());
    }

    @Override
    public List<AlbumDto> getAlbums(AlbumFilterDto albumFilterDto) {
        Stream<Album> filtered = ((List<Album>) albumRepository.findAll()).stream();

        return albumMapper.toDtoList(applyFilters(filtered, albumFilterDto));
    }

    @Override
    public List<AlbumDto> getAuthorsAlbums(Long authorId, AlbumFilterDto albumFilterDto) {
        Stream<Album> filteredAuthorsAlbums = albumRepository.findByAuthorId(authorId).stream();

        return albumMapper.toDtoList(applyFilters(filteredAuthorsAlbums, albumFilterDto));
    }

    @Override
    public AlbumDto addPost(Long postId, Long albumId, Long userId) {
        isAllowedChange(albumId, userId);
        Post post = postRepository.findById(postId).orElseThrow();
        Album album = albumRepository.findById(albumId).orElseThrow();
        album.getPosts().add(post);

        return albumMapper.toDto(albumRepository.save(album));
    }

    @Override
    public AlbumDto removePost(Long postId, Long albumId, Long userId) {
        isAllowedChange(albumId, userId);
        Album album = albumRepository.findById(albumId).orElseThrow();
        album.getPosts().removeIf(post -> post.getId().equals(postId));

        return albumMapper.toDto(albumRepository.save(album));
    }

    @Override
    public FavoriteAlbumsDto addToFavoriteAlbums(Long albumId, Long userId) {
        if (favoriteAlbumsRepository.existsByAlbumIdAndUserId(albumId, userId)) {
            String message = String.format("Album with id %s is already in favorite albums", albumId);
            log.error(message);
            throw new ForbiddenException(message);
        }
        FavoriteAlbums favoriteAlbums = FavoriteAlbums
                .builder()
                .userId(userId)
                .albumId(albumId)
                .createdAt(LocalDateTime.now())
                .build();

        return albumMapper.toFavoriteAlbumDto(favoriteAlbumsRepository.save(favoriteAlbums));
    }

    @Override
    public void removeFromFavoriteAlbum(Long albumId, Long userId) {

        if (!favoriteAlbumsRepository.existsByAlbumIdAndUserId(albumId, userId)) {
            String message = String.format("Album with id %s is not in favorite albums", albumId);
            log.error(message);
            throw new ForbiddenException(message);
        }

        favoriteAlbumsRepository.deleteByAlbumIdAndUserId(albumId, userId);
    }

    private void validateExistAlbumById(Long albumId) {
        if (!albumRepository.existsById(albumId)) {
            String message = String.format("Album with id %s does not exist", albumId);
            log.error(message);
            throw new DataValidationException(message);
        }
    }

    private List<Album> applyFilters(Stream<Album> filtered, AlbumFilterDto albumFilterDto) {
        for (AlbumFilter albumFilter : albumFilters) {
            if (albumFilter.isApplicable(albumFilterDto)) {
                filtered = albumFilter.apply(filtered, albumFilterDto);

            }
        }

        return filtered.toList();

    }

    private void isAllowedChange(Long albumId, Long userId) {
        if (!albumRepository.existsById(albumId)) {
            String message = String.format("Album with id %s does not exist", albumId);
            log.error(message);
            throw new DataValidationException(message);
        }
    }
}