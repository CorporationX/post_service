package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDTO;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ResponseStatusException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.FavoriteAlbum;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.adapter.AlbumRepositoryAdapter;
import faang.school.postservice.repository.adapter.FavoriteAlbumRepositoryAdapter;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.repository.specification.album.AlbumSpecification;
import faang.school.postservice.repository.specification.album.FavoriteAlbumSpecification;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepositoryAdapter albumRepositoryAdapter;
    private final AlbumMapper albumMapper;
    private final UserServiceClient userServiceClient;
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final FavoriteAlbumRepositoryAdapter favoriteAlbumRepositoryAdapter;
    private final UserContext userContext;

    @Transactional
    public AlbumDTO save(AlbumDTO albumDTO) {
        if (albumRepositoryAdapter.existsByTitleAndAuthorId(albumDTO.getTitle(), albumDTO.getAuthorId())) {
            throw new BadRequestException(
                    "An album with this title already exists");
        }
        validateUserExists(albumDTO.getAuthorId());
        Album album = albumMapper.toEntity(albumDTO);

        return albumMapper.toDto(albumRepositoryAdapter.save(album));
    }

    @Transactional
    public void addPostToAlbum(Long albumId, Long postId) {
        Long userId = userContext.getUserId();
        Post post = postRepositoryAdapter.getById(postId);
        Album album = albumRepositoryAdapter.getById(albumId);
        validateAlbumOwnership(userId, album, "Unable to add post to album");
        album.addPost(post);
    }

    @Transactional
    public void deletePostToAlbum(Long albumId, Long postId) {
        Long userId = userContext.getUserId();
        Album album = albumRepositoryAdapter.getById(albumId);
        validateAlbumOwnership(userId, album, "Unable to delete post from album");
        album.removePost(postId);
    }

    @Transactional
    public void addAlbumToFavourite(Long albumId) {
        Long userId = userContext.getUserId();
        validateUserExists(userId);
        Album album = albumRepositoryAdapter.getById(albumId);
        if (favoriteAlbumRepositoryAdapter.existsByAlbumAndAuthorId(album, userId)) {
            throw new BadRequestException("Album already added to favourites");
        }
        FavoriteAlbum favoriteAlbum = new FavoriteAlbum();
        favoriteAlbum.setAlbum(album);
        favoriteAlbum.setUserId(userId);
        favoriteAlbumRepositoryAdapter.save(favoriteAlbum);
    }

    @Transactional
    public void deleteByAlbumIdAndUserId(Long albumId) {
        Long userId = userContext.getUserId();
        Album album = albumRepositoryAdapter.getById(albumId);
        favoriteAlbumRepositoryAdapter.deleteByAlbumAndUserId(album, userId);
    }

    public AlbumDTO getById(Long albumId) {
        Album album = albumRepositoryAdapter.getById(albumId);
        return albumMapper.toDto(album);
    }

    public List<AlbumDTO> getAll(String title, LocalDate createdAt, Long authorId) {
        List<Specification<Album>> specs = new ArrayList<>();

        if (title != null) {
            specs.add(AlbumSpecification.getByTitle(title));
        }
        if (createdAt != null) {
            specs.add(AlbumSpecification.getByCreatedDate(createdAt));
        }

        if (authorId != null) {
            specs.add(AlbumSpecification.getByAuthor(authorId));
        }

        Specification<Album> spec = specs.stream()
                .reduce(Specification::and)
                .orElse(null);

        List<Album> albums = albumRepositoryAdapter.findAll(spec);
        return albumMapper.toDtoList(albums);
    }

    public List<AlbumDTO> getAllMyAlbums(String title, LocalDate createdAt) {
        Long userId = userContext.getUserId();
        List<Specification<Album>> specs = new ArrayList<>();

        if (title != null) {
            specs.add(AlbumSpecification.getByTitle(title));
        }
        if (createdAt != null) {
            specs.add(AlbumSpecification.getByCreatedDate(createdAt));
        }

        specs.add(AlbumSpecification.getByAuthor(userId));

        Specification<Album> spec = specs.stream()
                .reduce(Specification::and)
                .orElse(null);

        List<Album> albums = albumRepositoryAdapter.findAll(spec);
        return albumMapper.toDtoList(albums);
    }

    public List<AlbumDTO> getFavoriteAlbums(String title, LocalDate createdAt) {
        Long userId = userContext.getUserId();
        List<Specification<FavoriteAlbum>> specs = new ArrayList<>();

        if (title != null) {
            specs.add(FavoriteAlbumSpecification.getByTitle(title));
        }
        if (createdAt != null) {
            specs.add(FavoriteAlbumSpecification.getByCreatedDate(createdAt));
        }

        specs.add(FavoriteAlbumSpecification.getByAuthor(userId));

        Specification<FavoriteAlbum> spec = specs.stream()
                .reduce(Specification::and)
                .orElse(null);

        List<FavoriteAlbum> favoriteAlbums = favoriteAlbumRepositoryAdapter.findAll(spec);

        return favoriteAlbums.stream()
                .map(fav -> albumMapper.toDto(fav.getAlbum()))
                .collect(Collectors.toList());
    }

    @Transactional
    public AlbumDTO update(AlbumDTO albumDTO, Long albumId) {
        Long userId = userContext.getUserId();
        Album album = albumRepositoryAdapter.getById(albumId);
        validateAlbumOwnership(userId, album, "Unable to update album");
        if (albumRepositoryAdapter.existsByTitleAndAuthorId(albumDTO.getTitle(), albumDTO.getAuthorId())) {
            throw new BadRequestException(
                    "An album with this title already exists");
        }
        albumMapper.update(albumDTO, album);
        return albumMapper.toDto(album);
    }

    @Transactional
    public void delete(Long albumId) {
        Long userId = userContext.getUserId();
        Album album = albumRepositoryAdapter.getById(albumId);
        validateAlbumOwnership(userId, album, "Unable to delete album");
        albumRepositoryAdapter.delete(album);
    }

    private void validateAlbumOwnership(Long userId, Album album, String msg) {
        if (album.getAuthorId() != userId) {
            throw new BadRequestException(msg);
        }
    }

    private void validateUserExists(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        } catch (FeignException e) {
            throw new ResponseStatusException("User service is unavailable");
        }
    }
}