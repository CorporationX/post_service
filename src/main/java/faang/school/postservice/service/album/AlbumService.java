package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumValidator albumValidator;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        long userId = userContext.getUserId();
        boolean existsByTitleAndAuthorId = isExistsByTitleAndAuthorId(albumDto.getTitle(), userId);

        albumValidator.validateAlbumTitleUnique(existsByTitleAndAuthorId);

        Album albumToSave = albumMapper.toAlbum(albumDto);
        albumToSave.setAuthorId(userId);

        return albumMapper.toDto(albumRepository.save(albumToSave));
    }

    @Transactional
    public AlbumDto updateAlbum(AlbumDto albumDto) {
        Album albumToUpdate = getAlbumById(albumDto.getId());

        boolean existsByTitleAndAuthorId = isExistsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId());

        albumValidator.validationOfAlbumUpdate(albumDto, albumToUpdate, existsByTitleAndAuthorId);

        Album updatedAlbum = albumMapper.toAlbum(albumDto);
        updatedAlbum.setUpdatedAt(LocalDateTime.now());

        return albumMapper.toDto(albumRepository.save(updatedAlbum));
    }

    @Transactional
    public void deleteAlbum(Long albumId) {
        albumRepository.deleteById(albumId);
    }

    @Transactional
    public AlbumDto addPost(Long albumId, Long postId) {
        Long userId = userContext.getUserId();
        Album album = getAlbumByOwnerId(albumId, userId);

        Post post = getPost(postId);

        album.addPost(post);

        return albumMapper.toDto(albumRepository.save(album));
    }

    @Transactional
    public AlbumDto deletePost(Long albumId, Long postId) {
        Long userId = userContext.getUserId();
        Album album = getAlbumByOwnerId(albumId, userId);

        Post post = getPost(postId);

        album.removePost(post.getId());

        return albumMapper.toDto(albumRepository.save(album));
    }

    private Album getAlbumByOwnerId(Long albumId, Long userId) {
        return albumRepository.findByAuthorId(userId)
                .filter(a -> a.getId() == albumId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    private Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException("Album not found"));
    }

    private boolean isExistsByTitleAndAuthorId(String title, Long userId) {
        return albumRepository.existsByTitleAndAuthorId(title, userId);
    }
}
