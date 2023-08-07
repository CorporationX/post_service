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


    public void deletePostFromAlbum(long albumId, long postIdToDelete) {
        long userId = userContext.getUserId();
        Album album = deletePostFromAlbumValidation(userId, albumId, postIdToDelete);

        List<Post> posts = album.getPosts();
        List<Long> postsIds = posts.stream().map(Post::getId).toList();

        int id = postsIds.indexOf(postIdToDelete);

        if (id == -1) {
            throw new AlbumException(String.format("Post with id=%d is not found in album", postIdToDelete));
        }

        posts.remove(id);
        album.setPosts(posts);

        albumRepository.save(album);
    }

    public AlbumDto addPostToAlbum(long albumId, long postId) {
        long userId = userContext.getUserId();
        Album album = addPostToAlbumValidation(userId, albumId, postId);
        album.addPost(postRepository.findById(postId).get());

        albumRepository.save(album);

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

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        checkIfAuthorExists(albumDto);
        checkIfAlbumHasUniqueTitle(albumDto);

        Album album = albumMapper.toEntity(albumDto);
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());

        log.info("Created album: {}", album);
        return albumMapper.toDto(albumRepository.save(album));
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
}