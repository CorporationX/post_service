package faang.school.postservice.service;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import faang.school.postservice.repository.PostRepository;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final PostValidator postValidator;
    private final UserValidator userValidator;
    private final AlbumValidator albumValidator;
    private final AlbumMapper albumMapper;
    private final PostRepository postRepository;
    private final PostService postService;
    private final List<Filter<Album, AlbumFilterDto>> filters;

    public AlbumDto createAlbum(AlbumDto albumDto) {
        userValidator.checkUserExistence(albumDto.getAuthorId());

        Album album = albumMapper.toEntity(albumValidator.albumExistsByTitleAndAuthorId(albumDto));
        Album saveAlbum = albumRepository.save(album);

        return albumMapper.toDto(saveAlbum);
    }

    @Transactional
    public AlbumDto addPostToAlbum(long userId, long albumId, List<Long> postId) {
        Post post = (Post) postService.getPostsByIds(postId);
        Album album = findAlbumForUser(userId, albumId);
        album.addPost(post);
        albumRepository.save(album);

        return albumMapper.toDto(album);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id: %s not found", id)));
    }

    @Transactional
    public AlbumDto removePostFromAlbum(long userId, long albumId, long postId) {
        postValidator.validatePostExistsById(postId);
        Album album = findAlbumForUser(userId, albumId);
        album.removePost(postId);

        return albumMapper.toDto((albumRepository.save(album)));
    }

    public AlbumDto addAlbumToFavorites(long userId, long albumId) {
        AlbumDto existAlbum = findByAlbumId(albumId);
        albumRepository.addAlbumToFavorites(existAlbum.getId(), userId);

        return existAlbum;
    }

    public void deleteAlbumFromFavorites(long userId, long albumId) {
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    public AlbumDto findByAlbumId(long albumId) {
        return albumRepository.findById(albumId)
                .map(albumMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Album with id %d not found", albumId)));
    }

    public List<AlbumDto> getAlbumsForUserByFilter(long authorId, AlbumFilterDto albumFilterDto) {
        Stream<Album> albums = albumRepository.findByAuthorId(authorId);
        List<AlbumDto> result = filters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(albums,
                        (stream, filter) -> filter.apply(stream, albumFilterDto),
                        (s1, s2) -> s1)
                .map(albumMapper::toDto)
                .toList();
        log.info("Albums filtered by {}.", albumFilterDto);

        return result;
    }

    public List<AlbumDto> getAllAlbumsByFilter(AlbumFilterDto albumFilterDto) {
        Stream<Album> albums = StreamSupport.stream(albumRepository.findAll().spliterator(), false);
        List<AlbumDto> result = filters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(albums,
                        (stream, filter) -> filter.apply(stream, albumFilterDto),
                        (s1, s2) -> s1)
                .map(albumMapper::toDto)
                .toList();
        log.info("All albums filtered by {}.", albumFilterDto);

        return result;
    }

    public List<AlbumDto> getFavoriteAlbumsForUserByFilter(long userId, AlbumFilterDto filterDto) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        List<AlbumDto> result = filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(albums,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .map(albumMapper::toDto)
                .toList();
        log.info("Albums filtered by {}.", filterDto);

        return result;
    }

    public AlbumDto updateAlbum(AlbumUpdateDto albumUpdateDto) {
        Album updateAlbum = albumMapper.toEntity(findByAlbumId(albumUpdateDto.getId()));
        albumMapper.update(albumUpdateDto, updateAlbum);

        return albumMapper.toDto(albumRepository.save(updateAlbum));
    }

    public void deleteAlbum(long userId, long albumId) {
        Album albumForUser = findAlbumForUser(userId, albumId);
        albumRepository.deleteById(albumForUser.getId());
    }

    private Album findAlbumForUser(long userId, long albumId) {
        return albumRepository.findByAuthorId(userId)
                .filter(album -> album.getId() == (albumId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Album not found or doesn't belong to the user "));
    }
}
