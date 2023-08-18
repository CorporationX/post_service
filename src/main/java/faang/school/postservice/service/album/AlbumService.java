package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.album.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumValidator albumValidator;
    private final PostService postService;
    private final List<AlbumFilter> albumFilters;

    @Transactional
    public void createAlbum(AlbumDto albumDto, long userId) {
        albumValidator.validateAlbumToCreate(albumDto, userId);
        albumDto.setAuthorId(userId);
        albumRepository.save(albumMapper.toEntity(albumDto));
    }

    public void deleteAlbum(Long albumId, Long userId) {
        albumValidator.validateAuthor(albumId, userId);
        albumRepository.deleteById(albumId);
    }

    public void addPostToAlbum(Long albumId, Long postId, Long userId) {
        albumValidator.validatePostToAdd(albumId, postId, userId);
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new DataValidationException("Album not found"));
        Post post = postService.getPostById(postId);
        album.addPost(post);
    }

    @Transactional
    public void addToFavorite(Long albumId, Long userId) {
        albumValidator.validateUser(userId);
        albumValidator.validateAlbum(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    public void removeFromFavorite(Long albumId, Long userId) {
        albumValidator.validateUser(userId);
        albumValidator.validateAlbum(albumId);
        albumValidator.validateFavoriteAlbumToDelete(userId, albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    public AlbumDto getAlbum(Long albumId) {
        return albumMapper.toDto(albumRepository.findById(albumId)
                .orElseThrow(() -> new DataValidationException("Album not found")));
    }

    @Transactional
    public List<AlbumDto> getAllUserAlbums(Long userId, AlbumFilterDto albumFilterDto) {
        albumValidator.validateUser(userId);
        Stream<Album> streamAlbums = albumRepository.findByAuthorId(userId);
        return filterAlbums(streamAlbums, albumFilterDto);
    }

    public List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto) {
        Stream<Album> streamAlbums = StreamSupport.stream(albumRepository.findAll().spliterator(), false);
        return filterAlbums(streamAlbums, albumFilterDto);
    }

    @Transactional
    public List<AlbumDto> getAllFavorites(Long userId, AlbumFilterDto albumFilterDto) {
        albumValidator.validateUser(userId);
        Stream<Album> streamAlbums = albumRepository.findFavoriteAlbumsByUserId(userId);
        return filterAlbums(streamAlbums, albumFilterDto);
    }

    public void updateAlbum(AlbumDto albumDto) {
        albumRepository.save(albumMapper.toEntity(albumDto));
    }

    private List<AlbumDto> filterAlbums(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        List<AlbumFilter> filteredAlbum = albumFilters.stream()
                .filter(albumFilter -> albumFilter.isApplicable(albumFilterDto)).toList();

        for (AlbumFilter albumFilter : filteredAlbum) {
            albums = albumFilter.applyFilter(albums, albumFilterDto);
        }
        return albums.
                map(albumMapper::toDto)
                .toList();
    }
}