package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.album.AlbumValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private static final String POST_NOT_FOUND_MESSAGE = "Post not found";
    private static final String ALBUM_NOT_FOUND_MESSAGE = "Album not found";

    private final UserServiceClient userServiceClient;
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;

    @Transactional
    public AlbumDto createAlbum(long userId, AlbumDto albumDto) {
        AlbumValidator.checkAlbumDtoTitleAndDescriptionExist(albumDto);
        UserDto userDto = userServiceClient.getUser(userId);
        AlbumValidator.checkUserExist(userId, userDto);
        List<Album> albums = albumRepository.findByAuthorId(userId);
        AlbumValidator.checkAlbumNotExist(albumDto.getTitle(), albums);
        albumDto.setAuthorId(userId);
        Album album = albumMapper.toAlbum(albumDto);
        album.setCreatedAt(LocalDateTime.now());
        Album savedAlbum = albumRepository.save(album);
        return albumMapper.toAlbumDto(savedAlbum);
    }

    @Transactional
    public AlbumDto addPostToAlbum(long userId, long postId, long albumId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_MESSAGE));
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(() -> new EntityNotFoundException(ALBUM_NOT_FOUND_MESSAGE));
        AlbumValidator.checkAlbumAuthorWithUser(userId, album);
        AlbumValidator.checkPostInAlbum(post, album);
        album.addPost(post);
        Album savedAlbum = albumRepository.save(album);
        return albumMapper.toAlbumDto(savedAlbum);
    }

    @Transactional
    public void deletePostFromAlbum(long userId, long postId, long albumId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_MESSAGE));
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(() -> new EntityNotFoundException(ALBUM_NOT_FOUND_MESSAGE));
        AlbumValidator.checkAlbumAuthorWithUser(userId, album);
        album.removePost(postId);
        albumRepository.save(album);
    }

    public AlbumDto getAlbumById(long albumId) {
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(() -> new EntityNotFoundException(ALBUM_NOT_FOUND_MESSAGE));
        return albumMapper.toAlbumDto(album);
    }

    @Transactional
    public AlbumDto updateAlbum(long userId, AlbumDto albumDto) {
        AlbumValidator.checkAlbumDtoTitleAndDescriptionExist(albumDto);
        UserDto userDto = userServiceClient.getUser(userId);
        AlbumValidator.checkUserExist(userId, userDto);
        Album album = albumRepository.findByIdWithPosts(albumDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ALBUM_NOT_FOUND_MESSAGE));
        AlbumValidator.checkAlbumAuthorWithUser(userId, album);
        Album updatedAlbum = albumMapper.toAlbum(albumDto);
        updatedAlbum.setUpdatedAt(LocalDateTime.now());
        updatedAlbum.setAuthorId(userId);
        return albumMapper.toAlbumDto(albumRepository.save(updatedAlbum));
    }

    @Transactional
    public void deleteAlbum(long userId, long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException(ALBUM_NOT_FOUND_MESSAGE));
        AlbumValidator.checkAlbumAuthorWithUser(userId, album);
        albumRepository.deleteById(album.getId());
    }

    public List<AlbumDto> getAllAlbumsByAuthorIdWithFilters(long userId, AlbumFilterDto filters) {
        return albumRepository.findByAuthorId(userId).stream()
                .filter(album -> filters.getTitle() == null || album.getTitle().contains(filters.getTitle()))
                .filter(album -> {
                    if (filters.getCreatedAt() == null) {
                        return true;
                    } else if (filters.getCreatedBefore() != null && filters.getCreatedBefore()) {
                        return album.getCreatedAt().isBefore(filters.getCreatedAt());
                    } else {
                        return album.getCreatedAt().isAfter(filters.getCreatedAt());
                    }
                })
                .map(albumMapper::toAlbumDto)
                .collect(Collectors.toList());
    }

    public List<AlbumDto> getAllAlbumsWithFilters(AlbumFilterDto filters) {
        return albumRepository.findAllAlbums().stream()
                .filter(album -> filters.getTitle() == null || album.getTitle().contains(filters.getTitle()))
                .filter(album -> {
                    if (filters.getCreatedAt() == null) {
                        return true;
                    } else if (filters.getCreatedBefore() != null && filters.getCreatedBefore()) {
                        return album.getCreatedAt().isBefore(filters.getCreatedAt());
                    } else {
                        return album.getCreatedAt().isAfter(filters.getCreatedAt());
                    }
                })
                .map(albumMapper::toAlbumDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFavouriteAlbum(long userId, long albumId) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException(ALBUM_NOT_FOUND_MESSAGE));
        UserDto userDto = userServiceClient.getUser(userId);
        AlbumValidator.checkUserExist(userId, userDto);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    public void deleteFavouriteAlbum(long userId, long albumId) {
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    public List<AlbumDto> getFavouriteAlbumsByUserId(long userId, AlbumFilterDto filters) {
        return albumRepository.findFavoriteAlbumsByUserId(userId).stream()
                .filter(album -> filters.getTitle() == null || album.getTitle().contains(filters.getTitle()))
                .filter(album -> {
                    if (filters.getCreatedAt() == null) {
                        return true;
                    } else if (filters.getCreatedBefore() != null && filters.getCreatedBefore()) {
                        return album.getCreatedAt().isBefore(filters.getCreatedAt());
                    } else {
                        return album.getCreatedAt().isAfter(filters.getCreatedAt());
                    }
                })
                .map(albumMapper::toAlbumDto)
                .collect(Collectors.toList());
    }

}
