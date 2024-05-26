package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumValidator validator;
    private final UserContext userContext;
    private final PostService postService;
    private final PostMapper postMapper;
    private final List<AlbumFilter> filters;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        validator.validateUniqueTitle(albumDto);
        Album saved = albumRepository.save(albumMapper.toEntity(albumDto));
        return albumMapper.toDto(saved);
    }

    @Transactional
    public AlbumDto addPost(Long albumId, Long postId) {
        long userId = getUserId();
        Album album = getAlbumById(albumId);

        validator.validateAlbumAuthor(userId, album);

        Post post = postMapper.toEntity(postService.getPost(postId));
        album.addPost(post);
        return albumMapper.toDto(album);
    }

    @Transactional
    public void deletePost(Long albumId, Long postId) {
        long userId = getUserId();
        Album album = getAlbumById(albumId);

        validator.validateAlbumAuthor(userId, album);
        album.removePost(postId);
    }

    @Transactional
    public void addToFavorite(Long albumId) {
        long userId = getUserId();
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    public void removeFromFavorite(Long albumId) {
        long userId = getUserId();
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    @Transactional(readOnly = true)
    public AlbumDto getAlbum(Long albumId) {
        Album album = getAlbumById(albumId);
        return albumMapper.toDto(album);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserAlbums(AlbumFilterDto filterDto) {
        long userId = userContext.getUserId();
        List<AlbumDto> albums = albumRepository.findByAuthorId(userId)
                .map(albumMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        applyFilters(albums, filterDto);
        return albums;
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAllAlbums(AlbumFilterDto filterDto) {
        List<AlbumDto> albums = StreamSupport.stream(albumRepository.findAll().spliterator(), false)
                .map(albumMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        applyFilters(albums, filterDto);
        return albums;
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserFavoriteAlbums(AlbumFilterDto filterDto) {
        long userId = getUserId();
        List<AlbumDto> albums = albumRepository.findFavoriteAlbumsByUserId(userId)
                .map(albumMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        applyFilters(albums, filterDto);
        return albums;
    }

    @Transactional
    public AlbumDto updateAlbum(Long albumId, AlbumDto albumDto) {
        Album album = getAlbumById(albumId);
        validator.validateChangeAuthor(album, albumDto);

        if (!album.getTitle().equals(albumDto.getTitle())) {
            validator.validateUniqueTitle(albumDto);
        }

        album.setTitle(albumDto.getTitle());
        album.setDescription(albumDto.getDescription());
        return albumMapper.toDto(album);
    }

    @Transactional
    public void deleteAlbum(Long albumId) {
        long userId = getUserId();
        Album album = getAlbumById(albumId);
        validator.validateAlbumAuthor(userId, album);
        albumRepository.delete(album);
    }

    private void applyFilters(List<AlbumDto> albums, AlbumFilterDto filterDto) {
        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(albums, filterDto));
    }

    private Album getAlbumById(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album is not exist"));
    }

    private long getUserId() {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        return userId;
    }
}
