package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.album.AlbumValidator;
import faang.school.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumValidator albumValidator;
    private final UserValidator userValidator;
    private final PostRepository postRepository;

    public AlbumDto create(AlbumDto albumDto) {
        userValidator.validateUserExist(albumDto.getAuthorId());
        albumValidator.validateAlbumTitle(albumDto);

        Album savedAlbum = albumRepository.save(albumMapper.toEntity(albumDto));
        return albumMapper.toDto(savedAlbum);
    }

    public AlbumDto addPostToAlbum(long userId, long albumId, long postId) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserHasAccess(userId, album);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post doesn't exist by id: " + postId));

        album.addPost(post);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto deletePostFromAlbum(long userId, long albumId, long postId) {
        Album album = getAlbumFromRepository(albumId);
        albumValidator.validateIfUserHasAccess(userId, album);

        album.removePost(postId);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public void addAlbumToFavourites(AlbumDto albumDto) {
        long userId = albumDto.getAuthorId();
        userValidator.validateUserExist(userId);
        albumRepository.addAlbumToFavorites(albumDto.getId(), userId);
    }

    public void deleteAlbumToFavourites(AlbumDto albumDto) {
        long userId = albumDto.getAuthorId();
        userValidator.validateUserExist(userId);
        albumRepository.deleteAlbumFromFavorites(albumDto.getId(), userId);
    }

    private Album getAlbumFromRepository(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album doesn't exist by id: " + albumId));
    }
}
