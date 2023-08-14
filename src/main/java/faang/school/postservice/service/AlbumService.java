package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.album_filter.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final AlbumMapper albumMapper;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilters;

    @Transactional
    public AlbumDto createAlbum(AlbumCreateDto albumCreateDto) {
        if (userServiceClient.getUser(albumCreateDto.getAuthorId()) == null) {
            throw new DataValidationException("User is not found");
        }
        if (albumRepository.existsByTitleAndAuthorId(albumCreateDto.getTitle(), albumCreateDto.getAuthorId())) {
            throw new IllegalArgumentException("Album with this title already exists");
        }

        return albumMapper.toAlbumDto(albumRepository.save(albumMapper.toAlbumCreate(albumCreateDto)));
    }

    @Transactional
    public void addPostToAlbum(Long albumId, Long postId) {
        Album album = getAlbum(albumId);
        validateAlbumOwned(album);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (post.getAlbums().contains(album)) {
            throw new DataValidationException("Post already exists");
        }
        album.addPost(post);
        albumRepository.save(album);
    }

    @Transactional
    public void deletePostFromAlbum(Long albumId, Long postId) {
        Album album = getAlbum(albumId);
        validateAlbumOwned(album);

        album.removePost(postId);
        albumRepository.save(album);
    }

    @Transactional
    public void addAlbumToFavorites(Long albumId) {
        getAlbum(albumId);
        albumRepository.addAlbumToFavorites(albumId, userContext.getUserId());
    }

    @Transactional
    public void deleteAlbumFromFavorites(Long albumId) {
        getAlbum(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userContext.getUserId());
    }

    @Transactional
    public AlbumDto findByIdWithPosts(Long albumId) {
        Album album = getAlbum(albumId);

        return albumMapper.toAlbumDto(album);
    }

    public List<AlbumDto> findAListOfAllYourAlbums(AlbumFilterDto albumFilterDto) {
        Stream<Album> all = albumRepository.findByAuthorId(userContext.getUserId());

        return getFiltersAlbumDtos(albumFilterDto, all);
    }

    public List<AlbumDto> findListOfAllAlbumsInTheSystem(AlbumFilterDto albumFilterDto) {
        Stream<Album> all = albumRepository.findAll().stream();

        return getFiltersAlbumDtos(albumFilterDto, all);
    }

    public List<AlbumDto> findAListOfAllYourFavoriteAlbums(AlbumFilterDto albumFilterDto) {
        Stream<Album> all = albumRepository.findFavoriteAlbumsByUserId(userContext.getUserId());

        return getFiltersAlbumDtos(albumFilterDto, all);
    }

    @Transactional
    public AlbumDto updateAlbumAuthor(Long albumId, AlbumUpdateDto albumUpdateDto) {
        Album album = getAlbum(albumId);
        validateAlbumOwned(album);

        albumMapper.updateAlbum(albumUpdateDto, album);
        return albumMapper.toAlbumDto(albumRepository.save(album));
    }

    private void validateAlbumOwned(Album album) {
        if (userContext.getUserId() != album.getAuthorId()) {
            throw new DataValidationException("You are not author of this album");
        }
    }

    @Transactional
    public void deleteAlbum(Long albumId) {
        Album album = getAlbum(albumId);
        validateAlbumOwned(album);

        albumRepository.delete(album);
    }

    private Album getAlbum(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
    }

    private List<AlbumDto> getFiltersAlbumDtos(AlbumFilterDto albumFilterDto, Stream<Album> all) {
        List<AlbumFilter> albums = albumFilters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .toList();
        for (AlbumFilter filter : albums) {
            all = filter.apply(all, albumFilterDto);
        }
        return all.map(albumMapper::toAlbumDto).toList();
    }
}
