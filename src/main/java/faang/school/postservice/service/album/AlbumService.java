package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public AlbumDto getAlbum(long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumException("There is no album with id = " + id));

        log.info("Album with id = " + id + " was found");
        return albumMapper.toDto(album);
    }

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        checkIfAuthorExists(albumDto);
        checkIfAlbumHasUniqueTitle(albumDto);

        Album album = albumMapper.toEntity(albumDto);
        albumRepository.save(album);

        log.info("Created album: {}", album);
        return albumMapper.toDto(album);
    }

    @Transactional
    public AlbumDto updateAlbum(long albumId, AlbumDto updatedAlbum) {

        Album existingAlbum = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumException("Album not found"));

        if (existingAlbum.getAuthorId() != userContext.getUserId()) {
            throw new AlbumException("You can only update your own albums");
        }

        Album updatedEntity = albumMapper.toEntity(updatedAlbum);
        updatedAlbum.setAuthorId(existingAlbum.getAuthorId());

        existingAlbum.setTitle(updatedEntity.getTitle());
        existingAlbum.setDescription(updatedEntity.getDescription());
        existingAlbum.setPosts(new ArrayList<>(updatedEntity.getPosts()));
        existingAlbum.setUpdatedAt(LocalDateTime.now());

        log.info("update album with id {}", albumId);
        return albumMapper.toDto(existingAlbum);
    }

    @Transactional
    public DeleteResult deleteAlbum(Long albumId) {
        long userId = userContext.getUserId();
        Album album = albumRepository.findById(albumId).orElse(null);

        if (album == null) {
            return DeleteResult.NOT_FOUND;
        }

        if (album.getAuthorId() != userId) {
            return DeleteResult.NOT_AUTHORIZED;
        }

        log.info("Deleting album with id: {}", albumId);
        albumRepository.deleteById(albumId);
        return DeleteResult.DELETED;
    }

    @Transactional
    public void deletePostFromAlbum(long albumId, long postIdToDelete) {
        long userId = userContext.getUserId();
        Album album = deletePostFromAlbumValidation(userId, albumId, postIdToDelete);

        List<Post> posts = album.getPosts();
        boolean removed = posts.removeIf(post -> post.getId() == postIdToDelete);

        if (!removed) {
            throw new AlbumException(String.format("Post with id=%d is not found in album", postIdToDelete));
        }

        log.info("Post with id {} successfully removed from album with id {}", postIdToDelete, albumId);
    }

    @Transactional
    public AlbumDto addPostToAlbum(long albumId, long postId) {
        long userId = userContext.getUserId();
        Album album = addPostToAlbumValidation(userId, albumId, postId);
        album.addPost(postRepository.findById(postId).get());

        log.info("Post with id {} successfully added to album with id {}", postId, albumId);
        return albumMapper.toDto(album);
    }

    private Album validateAlbumAccess(long userId, long albumId) {
        Album foundAlbum = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumException("There is no album with such id"));

        if (userId != foundAlbum.getAuthorId()) {
            throw new AlbumException("You can perform this action only on your albums");
        }
        return foundAlbum;
    }

    private Album addPostToAlbumValidation(long userId, long albumId, long postId) {
        Album album = validateAlbumAccess(userId, albumId);

        if (album.getPosts().stream().anyMatch(post -> post.getId() == postId)) {
            throw new AlbumException("Post with id=" + postId + " is already added in album");
        }
        return album;
    }


    private Album deletePostFromAlbumValidation(long userId, long albumId, long postId) {
        Album album = validateAlbumAccess(userId, albumId);

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AlbumException("There is no post with such id"));

        return album;
    }


    private void checkIfAuthorExists(AlbumDto albumDto) {
        UserDto user = userServiceClient.getUser(albumDto.getAuthorId());
        if (user == null) {
            throw new AlbumException("There is no user with id " + albumDto.getAuthorId());
        }
    }

    private void checkIfAlbumHasUniqueTitle(AlbumDto albumDto) {
        albumRepository.findByAuthorId(albumDto.getAuthorId())
                .forEach(album -> {
                    if (album.getTitle().equals(albumDto.getTitle())) {
                        throw new AlbumException("Title of the album should be unique");
                    }
                });
    }

    @Transactional
    public void addAlbumToFavorites(long albumId) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);

        if (user == null) {
            throw new AlbumException("There is no user with id " + userId);
        }

        albumRepository.findFavoriteAlbumsByUserId(userId).forEach(album -> {
            if (album.getId() == albumId) {
                throw new AlbumException("This album is already added to favorites");
            }
        });

        albumRepository.addAlbumToFavorites(albumId,userId);
    }

    @Transactional
    public DeleteResult removeAlbumFromFavorites(long albumId) {
        long userId = userContext.getUserId();

        log.info("Deleting album from favorites albums with id: {}", albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
        return DeleteResult.DELETED;
    }
}