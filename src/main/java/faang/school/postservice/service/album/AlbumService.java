package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import faang.school.postservice.validation.album.AlbumValidator;
import faang.school.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumValidator albumValidator;
    private final UserValidator userValidator;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilters;

    public AlbumDto create(long userId, AlbumDto albumDto) {
        userValidator.validateUserExist(userId);
        albumValidator.validateAlbumTitle(albumDto);
        albumDto.setAuthorId(userId);
        albumDto.setAlbumVisibility(AlbumVisibility.PUBLIC);

        Album savedAlbum = albumRepository.save(albumMapper.toEntity(albumDto));
        return albumMapper.toDto(savedAlbum);
    }

    public AlbumDto getAlbum(long albumId) {
        Album album = getAlbumFromRepository(albumId);
        return albumMapper.toDto(album);
    }

    public List<AlbumDto> getUsersAlbums(long userId, AlbumFilterDto filters) {
        List<Album> albums = albumRepository.findByAuthorId(userId).collect(Collectors.toList());
        applyFilters(albums, filters);
        return albumMapper.toDto(albums);
    }

    public List<AlbumDto> getAllAlbums(AlbumFilterDto filters) {
        List<Album> albums = albumRepository.findAll();
        applyFilters(albums, filters);
        return albumMapper.toDto(albums);
    }

    public List<AlbumDto> getFavouriteAlbums(long userId, AlbumFilterDto filters) {
        List<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId).collect(Collectors.toList());
        applyFilters(albums, filters);
        return albumMapper.toDto(albums);
    }

    public AlbumDto update(long userId, AlbumDto albumDto) {
        Album album = getAlbumFromRepository(albumDto.getId());
        albumValidator.validateIfUserIsAuthor(userId, album);
        albumValidator.validateUpdatedAlbum(userId, albumDto);
        albumValidator.validateAlbumTitle(albumDto);

        Album updatedAndSavedAlbum = albumRepository.save(albumMapper.toEntity(albumDto));
        return albumMapper.toDto(updatedAndSavedAlbum);
    }

    public AlbumDto setPublicVisibility(long userId, long albumId) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserIsAuthor(userId, album);
        if (!AlbumVisibility.PUBLIC.equals(album.getAlbumVisibility())) {
            album.setAlbumVisibility(AlbumVisibility.PUBLIC);
        }

        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto setFollowersOnlyVisibility(long userId, long albumId) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserIsAuthor(userId, album);
        album.setAlbumVisibility(AlbumVisibility.FOLLOWERS_ONLY);

        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto setSelectedUsersOnlyVisibility(long userId, long albumId, List<Long> usersIds) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserIsAuthor(userId, album);
        album.setAlbumVisibility(AlbumVisibility.SELECTED_USERS_ONLY);
        album.setAllowedUsersIds(usersIds);

        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto setPrivateVisibility(long userId, long albumId) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserIsAuthor(userId, album);
        album.setAlbumVisibility(AlbumVisibility.PRIVATE);

        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto addPostToAlbum(long userId, long albumId, long postId) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserIsAuthor(userId, album);
        Post post = getPostFromRepository(postId);

        album.addPost(post);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public void addAlbumToFavourites(long userId, AlbumDto albumDto) {
        userValidator.validateUserExist(userId);
        albumRepository.addAlbumToFavorites(albumDto.getId(), userId);
    }

    public AlbumDto deletePostFromAlbum(long userId, long albumId, long postId) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserIsAuthor(userId, album);

        album.removePost(postId);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public void deleteAlbumFromFavourites(long userId, long albumId) {
        userValidator.validateUserExist(userId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    public void delete(long userId, long albumId) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserIsAuthor(userId, album);

        albumRepository.delete(album);
    }

    private Album getAlbumFromRepository(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album doesn't exist by id: " + albumId));
    }

    private Post getPostFromRepository(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post doesn't exist by id: " + postId));
    }

    private void applyFilters(List<Album> albums, AlbumFilterDto filters) {
        albumFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(albums, filters));
    }
}
