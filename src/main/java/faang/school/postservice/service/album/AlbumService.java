package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.event.AlbumCreatedEvent;
import faang.school.postservice.exception.AlbumException;
import faang.school.postservice.filters.album.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.album.AlbumCreatedEventPublisher;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.album.AlbumValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final PostService postService;
    private final AlbumValidator albumValidator;
    private final List<AlbumFilter> albumFilters;
    private final AlbumCreatedEventPublisher albumCreatedEventPublisher;

    public AlbumDto create(AlbumDto albumDto) {
        albumValidator.validateByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId());
        Album album = albumMapper.toEntity(albumDto);
        album = albumRepository.save(album);

        albumCreatedEventPublisher.publish(AlbumCreatedEvent.builder()
                .albumTitle(albumDto.getTitle())
                .userId(albumDto.getAuthorId())
                .albumId(album.getId())
                .build());

        return albumMapper.toDto(album);
    }

    public AlbumDto addPost(Long albumId, Long postId) {
        Post post = postService.findEntityById(postId);
        Album album = getValidAlbum(albumId);
        album.addPost(post);
        albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    public AlbumDto deletePost(Long albumId, Long postId) {
        Album album = getValidAlbum(albumId);
        album.removePost(postId);
        albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    public void addAlbumToFavorites(Long albumId, Long userId) {
        getValidAlbum(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    public void removeAlbumFromFavorites(Long albumId, Long userId) {
        getValidAlbum(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    public AlbumDto get(Long albumId) {
        return albumMapper.toDto(getValidAlbum(albumId));
    }

    public List<AlbumDto> getAlbums(Long userId, AlbumFilterDto filters) {
        albumValidator.validateUser(userId);
        Stream<Album> albums = albumRepository.findByAuthorId(userId);
        return albumFilters.stream().filter(filter -> filter.isApplicable(filters))
                        .flatMap(filter -> filter.apply(albums, filters)).map(albumMapper::toDto).toList();
    }

    public List<AlbumDto> getFavorites(Long userId, AlbumFilterDto filters) {
        albumValidator.validateUser(userId);
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        return albumFilters.stream().filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(albums, filters)).map(albumMapper::toDto).toList();
    }

    public AlbumDto update(AlbumDto albumDto) {
        Album album = getValidAlbum(albumDto.getId());
        albumMapper.update(albumDto, album);
        albumRepository.save(album);

        return albumMapper.toDto(album);
    }

    public void delete(Long albumId, Long userId) {
        albumValidator.validateUser(userId);
        Album album = getValidAlbum(albumId);

        if (album.getAuthorId() == userId) {albumRepository.delete(album);}
    }

    public Album getValidAlbum(Long albumId) {
        return albumRepository.findByIdWithPosts(albumId).orElseThrow(() -> {
            log.error("Альбом {} не существует", albumId);
            return new AlbumException("Не существующий альбом");
        });
    }
}

