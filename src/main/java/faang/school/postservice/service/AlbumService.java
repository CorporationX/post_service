package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.event.AlbumCreatedEvent;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.handler.EntityHandler;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.redisPublisher.AlbumCreateEventPublisher;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.AlbumValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final UserContext userContext;
    private final AlbumMapper albumMapper;
    private final EntityHandler entityHandler;
    private final UserValidator userValidator;
    private final PostValidator postValidator;
    private final AlbumValidator albumValidator;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilterList;
    private final AlbumCreateEventPublisher albumCreateEventPublisher;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        long requesterId = userContext.getUserId();
        userValidator.validateFollowersExistence(albumDto.getAllowedUserIds());
        albumValidator.validateAlbumTitleDoesNotDuplicatePerAuthor(requesterId, albumDto.getTitle());
        Album album = albumMapper.toEntity(albumDto);
        album.setAuthorId(requesterId);
        Album savedAlbum = albumRepository.save(album);

        albumCreateEventPublisher.publish(AlbumCreatedEvent.builder()
                .albumTitle(albumDto.getTitle())
                .userId(requesterId)
                .albumId(savedAlbum.getId())
                .build());
        return albumMapper.toDto(savedAlbum);
    }

    @Transactional
    public void addPostToAlbum(long postId, long albumId) {
        long requesterId = userContext.getUserId();
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        Post postToAdd = entityHandler.getOrThrowException(Post.class, postId, () -> postRepository.findById(postId));
        albumValidator.validateAlbumBelongsToRequester(requesterId, album);
        album.addPost(postToAdd);
        albumRepository.save(album);
    }

    @Transactional
    public void removePostFromAlbum(long postId, long albumId) {
        long requesterId = userContext.getUserId();
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        postValidator.validatePostExistence(postId);
        albumValidator.validateAlbumBelongsToRequester(requesterId, album);
        album.removePost(postId);
        albumRepository.save(album);
    }

    @Transactional
    public void addAlbumToFavourites(long albumId) {
        long requesterId = userContext.getUserId();
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        albumValidator.validateVisibilityToRequester(requesterId, album);
        albumRepository.addAlbumToFavorites(albumId, requesterId);
    }

    @Transactional
    public void removeAlbumFromFavourites(long albumId) {
        long requesterId = userContext.getUserId();
        albumValidator.validateAlbumExistence(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, requesterId);
    }

    @Transactional(readOnly = true)
    public AlbumDto getAlbumById(long albumId) {
        long requesterId = userContext.getUserId();
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        albumValidator.validateVisibilityToRequester(requesterId, album);
        return albumMapper.toDto(album);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAuthorFilteredAlbums(long authorId, AlbumFilterDto albumFilterDto) {
        long requesterId = userContext.getUserId();
        Stream<Album> allMatchedAlbums = albumRepository.findByAuthorId(authorId);
        allMatchedAlbums = getFilteredAlbums(allMatchedAlbums, requesterId, albumFilterDto);
        return allMatchedAlbums.map(albumMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAllFilteredAlbums(AlbumFilterDto albumFilterDto) {
        long requesterId = userContext.getUserId();
        Stream<Album> allMatchedAlbums = albumRepository.findAll().stream();
        allMatchedAlbums = getFilteredAlbums(allMatchedAlbums, requesterId, albumFilterDto);
        return allMatchedAlbums.map(albumMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserFavoriteAlbums(AlbumFilterDto albumFilterDto) {
        long requesterId = userContext.getUserId();
        Stream<Album> allMatchedAlbums = albumRepository.findFavoriteAlbumsByUserId(requesterId);
        allMatchedAlbums = getFilteredAlbums(allMatchedAlbums, requesterId, albumFilterDto);
        return allMatchedAlbums.map(albumMapper::toDto).toList();
    }

    @Transactional
    public AlbumDto updateAlbum(long albumId, AlbumDto albumDto) {
        long requesterId = userContext.getUserId();
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        albumValidator.validateAlbumBelongsToRequester(requesterId, album);
        entityHandler.updateNonNullFields(albumDto, album);
        album.setUpdatedAt(LocalDateTime.now());
        albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    @Transactional
    public void deleteAlbum(long albumId) {
        long requesterId = userContext.getUserId();
        Album album = entityHandler.getOrThrowException(Album.class, albumId, () -> albumRepository.findById(albumId));
        albumValidator.validateAlbumBelongsToRequester(requesterId, album);
        albumRepository.delete(album);
    }

    private Stream<Album> getFilteredAlbums(Stream<Album> albumStream, long requesterId, AlbumFilterDto albumFilterDto) {
        for (AlbumFilter albumFilter : albumFilterList) {
            albumStream = albumFilter.filter(albumStream, albumFilterDto);
        }
        return albumStream.filter(album -> albumValidator.isVisibleToRequester(requesterId, album));
    }
}