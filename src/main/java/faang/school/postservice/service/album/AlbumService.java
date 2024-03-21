package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.validator.album.AlbumValidator;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumValidator albumValidator;
    private final List<AlbumFilter> albumFilters;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        Stream<Album> albumStream = albumRepository.findByAuthorId(albumDto.getAuthorId());
        albumValidator.validate(albumStream, albumDto);
        Album album = albumRepository.save(albumMapper.toEntity(albumDto));
        return albumMapper.toDto(album);
    }

    public void addPost(long albumId, Post post) {
        Album album = albumRepository.findById(albumId).orElseThrow(() ->
                new DataValidationException("Такого альбома нет"));
        album.addPost(post);
        albumRepository.save(album);
    }

    public void removePost(long albumId, long postId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() ->
                new DataValidationException("Такого альбома нет"));
        album.removePost(postId);
        albumRepository.save(album);
    }

    public AlbumDto getAlbum(long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() ->
                new DataValidationException("Такого альбома нет"));
        return albumMapper.toDto(album);
    }

    public AlbumDto updateAlbum(long albumId, AlbumUpdateDto albumUpdateDto) {
        Stream<Album> albumStream = albumRepository.findByAuthorId(albumUpdateDto.getAuthorId());
        albumValidator.validate(albumStream, albumUpdateDto);
        Album album = albumRepository.findById(albumId).orElse(null);
        album.setId(albumId);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public void deleteAlbum(long albumId) {
        albumRepository.deleteById(albumId);
    }

    public List<AlbumDto> getAlbumsByFilter(AlbumFilterDto albumFilterDto) {
        List<Album> albums = albumRepository.findAll();
        return filterAlbumToList(albums, albumFilterDto);
    }

    public List<AlbumDto> filterAlbumToList(List<Album> albums, AlbumFilterDto albumFilterDto) {
        if (albumFilterDto == null) {
            return albums.stream().map(albumMapper::toDto).collect(Collectors.toList());
        } else {
            Stream<Album> stream = albums.stream();
            List<Album> filterAlbum = albumFilter(stream, albumFilterDto).toList();
            return filterAlbum.stream().map(albumMapper::toDto).collect(Collectors.toList());
        }
    }

    public Stream<Album> albumFilter(Stream<Album> albumStream, AlbumFilterDto albumFilterDto) {
        for (AlbumFilter albumFilter : albumFilters) {
            if (albumFilter.isApplicable(albumFilterDto)) {
                albumStream = albumFilter.apply(albumStream, albumFilterDto);
            }
        }
        return albumStream;
    }

    public void addAlbumToFavorites(long albumId, long userId) {
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    public void deleteAlbumFromFavorites(long albumId, long userId) {
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

}
