package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.filter.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.validation.UserValidationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilters;
    private final UserValidationService userValidationService;

    @Override
    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        userValidationService.validateUserExists(albumDto.getAuthorId());
        if (albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())) {
            throw new DataValidationException("Album with such title already exists for this author id=%d"
                    .formatted(albumDto.getAuthorId()));
        }
        Album album = albumMapper.toEntity(albumDto);
        albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    @Override
    public void deleteAlbum(long albumId, long userId) {
        Album album = findById(albumId);
        validateAuthorIsOwner(album, userId);
        albumRepository.delete(album);
    }

    @Override
    @Transactional
    public void addPostInAlbum(long albumId, long postId, long userId) {
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id=%d".formatted(albumId)));
        validateAuthorIsOwner(album, userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id=%d".formatted(postId)));
        if (!album.getPosts().contains(post)) {
            album.getPosts().add(post);
            post.getAlbums().add(album);
            albumRepository.save(album);
        }
    }

    @Override
    @Transactional
    public void deletePostFromAlbum(long albumId, long postId, long userId) {
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id=%d".formatted(albumId)));
        validateAuthorIsOwner(album, userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id=%d".formatted(postId)));
        album.getPosts().remove(post);
        post.getAlbums().remove(album);
        albumRepository.save(album);
    }

    @Override
    @Transactional
    public void addAlbumToFavorite(long albumId, long userId) {
        userValidationService.validateUserExists(userId);
        if (!albumRepository.existsById(albumId)) {
            throw new EntityNotFoundException("Album not found with id=%d".formatted(albumId));
        }
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Override
    public void deleteAlbumFromFavorite(long albumId, long userId) {
        userValidationService.validateUserExists(userId);
        if (!albumRepository.existsById(albumId)) {
            throw new EntityNotFoundException("Album not found with id=%d".formatted(albumId));
        }
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDto getAlbumById(long albumId) {
        Album album = findById(albumId);
        return albumMapper.toDto(album);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDto> getAllUserAlbums(long authorId, AlbumFilterDto albumFilterDto) {
        List<Album> albums = albumRepository.findByAuthorId(authorId);
        return filterAlbums(albums.stream(), albumFilterDto)
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto) {
        Iterable<Album> allAlbums = albumRepository.findAll();
        return filterAlbums(StreamSupport.stream(allAlbums.spliterator(), false), albumFilterDto)
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDto> getAllFavoriteAlbums(long userId, AlbumFilterDto albumFilterDto) {
        List<Album> favoriteAlbums = albumRepository.findFavoriteAlbumsByUserId(userId);
        return filterAlbums(favoriteAlbums.stream(), albumFilterDto)
                .map(albumMapper::toDto)
                .toList();
    }

    private Stream<Album> filterAlbums(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albumFilters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(albums,
                        (currentStream, filter) -> filter.apply(currentStream, albumFilterDto),
                        Stream::concat);
    }

    @Override
    @Transactional
    public AlbumDto updateAlbum(AlbumDto albumDto) {
        Album existingAlbum = findById(albumDto.getId());
        validateAuthorIsOwner(existingAlbum, albumDto.getAuthorId());
        existingAlbum.setTitle(albumDto.getTitle());
        existingAlbum.setDescription(albumDto.getDescription());
        albumRepository.save(existingAlbum);
        return albumMapper.toDto(existingAlbum);
    }

    private void validateAuthorIsOwner(Album album, long userId) {
        if (!album.getAuthorId().equals(userId)) {
            throw new DataValidationException("User with ID=%d cannot modify or delete this album".formatted(userId));
        }
    }

    private Album findById(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id=%d ".formatted(albumId)));
    }
}