package faang.school.postservice.service;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.filter.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumMapper albumMapper;
    private final UserValidator userValidator;
    private final PostValidator postValidator;
    private final AlbumValidator albumValidator;
    private final AlbumRepository albumRepository;
    private final List<AlbumFilter> albumFilterList;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        userValidator.validateUserExistence(albumDto.getAuthorId());
        albumValidator.validateAlbumTitleDoesNotDuplicatePerAuthor(albumDto.getAuthorId(), albumDto.getTitle());
        Album album = albumMapper.toEntity(albumDto);
        return albumMapper.toDto(albumRepository.save(album));
    }

    @Transactional
    public void addPostToAlbum(long authorId, long postId, long albumId) {
        Album album = albumValidator.validateAlbumExistence(albumId);
        Post postToAdd = postValidator.validatePostExistence(postId);
        albumValidator.validateAlbumBelongsToAuthor(authorId, album);
        album.addPost(postToAdd);
    }

    @Transactional
    public void removePostFromAlbum(long authorId, long postId, long albumId) {
        Album album = albumValidator.validateAlbumExistence(albumId);
        postValidator.validatePostExistence(postId);
        albumValidator.validateAlbumBelongsToAuthor(authorId, album);
        album.removePost(postId);
    }

    @Transactional
    public void addAlbumToFavourites(long albumId, long userId) {
        userValidator.validateUserExistence(userId);
        albumValidator.validateAlbumExistence(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    public void removeAlbumFromFavourites(long albumId, long userId) {
        userValidator.validateUserExistence(userId);
        albumValidator.validateAlbumExistence(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    @Transactional(readOnly = true)
    public AlbumDto getAlbumById(long albumId) {
        Album album = albumValidator.validateAlbumExistence(albumId);
        return albumMapper.toDto(album);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAuthorFilteredAlbums(long authorId, AlbumFilterDto albumFilterDto) {
        Stream<Album> allMatchedAlbums = albumRepository.findByAuthorId(authorId);
        return getFilteredAlbumDtoList(allMatchedAlbums, albumFilterDto);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAllFilteredAlbums(AlbumFilterDto albumFilterDto) {
        Stream<Album> allAlbums = StreamSupport.stream(albumRepository.findAll().spliterator(), false);
        return getFilteredAlbumDtoList(allAlbums, albumFilterDto);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserFavoriteAlbums(long userId, AlbumFilterDto albumFilterDto) {
        Stream<Album> favoriteAlbumsByUserId = albumRepository.findFavoriteAlbumsByUserId(userId);
        return getFilteredAlbumDtoList(favoriteAlbumsByUserId, albumFilterDto);
    }

    @Transactional
    public AlbumDto updateAlbum(long albumId, AlbumDto albumDto) {
        Album album = albumValidator.validateAlbumExistence(albumId);
        albumValidator.validateAlbumBelongsToAuthor(albumDto.getAuthorId(), album);
        album.setDescription(albumDto.getDescription());
        album.setTitle(albumDto.getTitle());
        return albumMapper.toDto(album);
    }

    @Transactional
    public void deleteAlbum(long albumId, long authorId) {
        Album album = albumValidator.validateAlbumExistence(albumId);
        albumValidator.validateAlbumBelongsToAuthor(authorId, album);
        albumRepository.delete(album);
    }

    private List<AlbumDto> getFilteredAlbumDtoList(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        for (AlbumFilter albumFilter : albumFilterList) {
            albumStream = albumFilter.filter(albumStream, albumFilterDto);
        }
        return albumStream.map(albumMapper::toDto).toList();
    }
}