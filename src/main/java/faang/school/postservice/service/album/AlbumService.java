package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.album.AlbumFilter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumValidator albumValidator;
    private final UserContext userContext;
    private final List<AlbumFilter> albumFilters;

    @Transactional(readOnly = true)
    public AlbumDto getAlbum(Long albumId) {
        return albumMapper.toDto(getAlbumById(albumId));
    }

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
        Album album = getAlbumByIdAndOwnerId(albumId, userId);

        Post post = getPost(postId);

        album.addPost(post);

        return albumMapper.toDto(albumRepository.save(album));
    }

    @Transactional
    public AlbumDto deletePost(Long albumId, Long postId) {
        Long userId = userContext.getUserId();
        Album album = getAlbumByIdAndOwnerId(albumId, userId);

        Post post = getPost(postId);

        album.removePost(post.getId());

        return albumMapper.toDto(albumRepository.save(album));
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getMyAlbums(AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        Stream<Album> albums = albumRepository.findByAuthorId(userId);
        return getFilteredAlbums(albumFilterDto, new ArrayList<>(albums.toList()));
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getMyFavouritesAlbums(AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        return getFilteredAlbums(albumFilterDto, new ArrayList<>(albums.toList()));
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAlbumsByFilter(AlbumFilterDto albumFilterDto){
        List<Album> albums = albumRepository.findAll();
        return getFilteredAlbums(albumFilterDto, albums);
    }

    private List<AlbumDto> getFilteredAlbums(AlbumFilterDto albumFilterDto, List<Album> albums) {
        albumFilters.stream()
                .filter(albumFilter -> albumFilter.isApplicable(albumFilterDto))
                .forEach(albumFilter -> albumFilter.apply(albums, albumFilterDto));
        return albums.stream().map(albumMapper::toDto).toList();
    }

    @Transactional
    public void addAlbumToFavourite(Long albumId) {
        long userId = userContext.getUserId();
        Album album = getAlbumByIdAndOwnerId(albumId, userId);
        boolean exist = albumRepository.existInFavorites(album.getId(), userId);
        albumValidator.vaidateExistsInFavorites(exist);
        albumRepository.addAlbumToFavorites(album.getId(), userId);
    }

    @Transactional
    public void deleteAlbumFromFavorites(Long albumId) {
        long userId = userContext.getUserId();
        Album album = getAlbumByIdAndOwnerId(albumId, userId);
        albumRepository.deleteAlbumFromFavorites(album.getId(), userId);
    }

    private Album getAlbumByIdAndOwnerId(Long albumId, Long userId) {
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
