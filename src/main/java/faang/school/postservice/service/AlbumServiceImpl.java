package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.filter.albumvisibility.AlbumVisibilityFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.album.AlbumValidator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("albumServiceImpl")
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private static final String POST_NOT_FOUND_MESSAGE = "Post with id = %d not found";
    private static final String ALBUM_NOT_FOUND_MESSAGE = "Album with id = %d not found";

    private final AlbumRepository albumRepository;
    private final UserContext userContext;
    private final List<AlbumVisibilityFilter> filters;
    private final UserServiceClient userServiceClient;
    private final AlbumMapper albumMapper;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilters;
    private final AlbumValidator albumValidator;

    private Map<AlbumVisibility, AlbumVisibilityFilter> albumVisibilities;

    @PostConstruct
    void initAlbumVisibilities() {
        this.albumVisibilities = filters.stream()
                .collect(Collectors.toMap(
                        AlbumVisibilityFilter::getAlbumVisibility,
                        Function.identity()
                ));
    }

    @Override
    public AlbumResponseDto getAlbumById(long id) {
        Album album = albumRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", id)));
        return albumVisibilities.get(album.getAlbumVisibility()).apply(album);
    }

    @Override
    public List<AlbumResponseDto> getAlbumsByAuthorId(long authorId) {
        return albumRepository.findByAuthorId(authorId).stream()
                .map(album -> albumVisibilities.get(album.getAlbumVisibility()).apply(album))
                .toList();
    }

    @Transactional
    @Override
    public AlbumResponseDto updateAlbumVisibility(long id, AlbumVisibility albumVisibility) {
        Album album = albumRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", id)));
        long userId = userContext.getUserId();
        validateAuthor(userId, album);
        album.setAlbumVisibility(albumVisibility);
        Album savedAlbum = albumRepository.save(album);
        return albumVisibilities.get(savedAlbum.getAlbumVisibility()).apply(savedAlbum);
    }

    @Transactional
    @Override
    public List<Long> addUsersForAccessAlbum(long albumId, AlbumUsersDto albumUsersDto) {
        Album album = albumRepository.findById(albumId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", albumId)));
        long userId = userContext.getUserId();
        validateAuthor(userId, album);
        checkVisibilityForAlbum(album);
        albumUsersDto.usersIds().forEach(id -> albumRepository.addUserForVisibilityAtAlbum(album.getId(), id));
        return albumUsersDto.usersIds();
    }

    private void validateAuthor(long userId, Album album) {
        if (userId != album.getAuthorId()) {
            log.error("User with id = {} isn't author for album with id = {}", userId, album.getId());
            throw new AlbumAccessDeniedException(
                    String.format("User with id = %d isn't author for album with id = %d", userId, album.getId()));
        }
    }

    private void checkVisibilityForAlbum(Album album) {
        if (!AlbumVisibility.SELECTED.equals(album.getAlbumVisibility())) {
            log.error("Visibility isn't {} in album with id = {}", AlbumVisibility.SELECTED, album.getId());
            throw new IllegalArgumentException(
                    String.format("Needed selected_users visibility for add users for access. Album: %d", album.getId()));
        }
    }

    @Transactional
    public AlbumDto createAlbum(long userId, AlbumDto albumDto) {
        albumValidator.checkAlbumDtoTitleAndDescriptionExist(albumDto);
        UserDto userDto = userServiceClient.getUser(userId);
        albumValidator.checkUserExist(userId, userDto);
        List<Album> albums = albumRepository.findByAuthorId(userId);
        albumValidator.checkAlbumNotExist(albumDto.getTitle(), albums);
        albumDto.setAuthorId(userId);
        Album album = albumMapper.toAlbum(albumDto);
        album.setCreatedAt(LocalDateTime.now());
        Album savedAlbum = albumRepository.save(album);
        return albumMapper.toAlbumDto(savedAlbum);
    }

    @Transactional
    public AlbumDto addPostToAlbum(long userId, long postId, long albumId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(POST_NOT_FOUND_MESSAGE, postId)));
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(() -> new EntityNotFoundException(ALBUM_NOT_FOUND_MESSAGE));
        albumValidator.checkAlbumAuthorWithUser(userId, album);
        albumValidator.checkPostInAlbum(post, album);
        album.addPost(post);
        Album savedAlbum = albumRepository.save(album);
        return albumMapper.toAlbumDto(savedAlbum);
    }

    @Transactional
    public void deletePostFromAlbum(long userId, long postId, long albumId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(POST_NOT_FOUND_MESSAGE, postId)));
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ALBUM_NOT_FOUND_MESSAGE, albumId)));
        albumValidator.checkAlbumAuthorWithUser(userId, album);
        album.removePost(postId);
        albumRepository.save(album);
    }

    public AlbumDto getAlbumByIdReturnAlbumDto(long albumId) {
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ALBUM_NOT_FOUND_MESSAGE, albumId)));
        return albumMapper.toAlbumDto(album);
    }

    @Transactional
    public AlbumDto updateAlbum(long userId, AlbumDto albumDto) {
        albumValidator.checkAlbumDtoTitleAndDescriptionExist(albumDto);
        UserDto userDto = userServiceClient.getUser(userId);
        albumValidator.checkUserExist(userId, userDto);
        Album album = albumRepository.findByIdWithPosts(albumDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(ALBUM_NOT_FOUND_MESSAGE, albumDto.getId())));
        albumValidator.checkAlbumAuthorWithUser(userId, album);
        Album updatedAlbum = albumMapper.toAlbum(albumDto);
        updatedAlbum.setUpdatedAt(LocalDateTime.now());
        updatedAlbum.setAuthorId(userId);
        return albumMapper.toAlbumDto(albumRepository.save(updatedAlbum));
    }

    @Transactional
    public void deleteAlbum(long userId, long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ALBUM_NOT_FOUND_MESSAGE, albumId)));
        albumValidator.checkAlbumAuthorWithUser(userId, album);
        albumRepository.deleteById(album.getId());
    }

    public List<AlbumDto> getAllAlbumsByAuthorIdWithFilters(long userId, AlbumFilterDto filters) {
        return filterAlbums(albumRepository.findByAuthorId(userId), filters);
    }

    public List<AlbumDto> getAllAlbumsWithFilters(AlbumFilterDto filters) {
        return filterAlbums(albumRepository.findAllAlbums(), filters);
    }

    @Transactional
    public void addFavouriteAlbum(long userId, long albumId) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ALBUM_NOT_FOUND_MESSAGE, albumId)));
        UserDto userDto = userServiceClient.getUser(userId);
        albumValidator.checkUserExist(userId, userDto);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    public void deleteFavouriteAlbum(long userId, long albumId) {
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    public List<AlbumDto> getFavouriteAlbumsByUserId(long userId, AlbumFilterDto filters) {
        return filterAlbums(albumRepository.findFavoriteAlbumsByUserId(userId), filters);
    }

    public List<AlbumDto> filterAlbums(@NotNull List<Album> albums, AlbumFilterDto filters) {
        Stream<Album> filteredAlbums = albums.stream();

        for (AlbumFilter albumFilter : albumFilters) {
            if (albumFilter.isApplicable(filters)) {
                filteredAlbums = albumFilter.apply(filteredAlbums, filters);
            }
        }
        return filteredAlbums
                .map(albumMapper::toAlbumDto)
                .toList();
    }

}