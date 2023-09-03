package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumDtoResponse;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.exception.DataValidException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
            throw new DataValidException("User is not found");
        }
        if (albumRepository.existsByTitleAndAuthorId(albumCreateDto.getTitle(), albumCreateDto.getAuthorId())) {
            throw new IllegalArgumentException("Album with this title already exists");
        }

        Album albumToCreate = albumMapper.toAlbumCreate(albumCreateDto);
        Album savedAlbum = albumRepository.save(albumToCreate);
        return albumMapper.toAlbumDto(savedAlbum);
    }

    @Transactional
    public void addPostToAlbum(Long albumId, Long postId) {
        Album album = getAlbumOrThrowException(albumId);
        validateAlbumOwned(album);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (post.getAlbums().contains(album)) {
            throw new DataValidException("Post already exists");
        }
        album.getPosts().add(post);
        albumRepository.save(album);
    }

    @Transactional
    public void deletePostFromAlbum(Long albumId, Long postId) {
        Album album = getAlbumOrThrowException(albumId);
        validateAlbumOwned(album);

        album.getPosts().removeIf(post -> post.getId() == postId);
        albumRepository.save(album);
    }

    @Transactional
    public void addAlbumToFavorites(Long albumId) {
        getAlbumOrThrowException(albumId);
        albumRepository.addAlbumToFavorites(albumId, userContext.getUserId());
    }

    @Transactional
    public void deleteAlbumFromFavorites(Long albumId) {
        getAlbumOrThrowException(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userContext.getUserId());
    }

    @Transactional
    public AlbumDtoResponse findAlbumById(Long albumId) {
        Album album = getAlbumOrThrowException(albumId);

        return processAlbumToDto(album);
    }

    public List<AlbumDtoResponse> findAListOfAllYourAlbums(AlbumFilterDto albumFilterDto) {
        Stream<Album> albumStream = albumRepository.findByAuthorId(userContext.getUserId());

        return getFiltersAlbumDtos(albumFilterDto, albumStream);
    }

    public List<AlbumDtoResponse> findListOfAllAlbumsInTheSystem(AlbumFilterDto albumFilterDto) {
        Stream<Album> albumStream = albumRepository.findAll().stream();

        return getFiltersAlbumDtos(albumFilterDto, albumStream);
    }

    public List<AlbumDtoResponse> findAListOfAllYourFavoriteAlbums(AlbumFilterDto albumFilterDto) {
        Stream<Album> albumStream = albumRepository.findFavoriteAlbumsByUserId(userContext.getUserId());

        return getFiltersAlbumDtos(albumFilterDto, albumStream);
    }

    @Transactional
    public AlbumDto updateAlbumAuthor(Long albumId, AlbumUpdateDto albumUpdateDto) {
        Album album = getAlbumOrThrowException(albumId);
        validateAlbumOwned(album);

        albumMapper.updateAlbum(albumUpdateDto, album);
        return albumMapper.toAlbumDto(albumRepository.save(album));
    }

    @Transactional
    public void deleteAlbum(Long albumId) {
        Album album = getAlbumOrThrowException(albumId);
        validateAlbumOwned(album);

        albumRepository.delete(album);
    }

    private void validateAlbumOwned(Album album) {
        if (userContext.getUserId() != album.getAuthorId()) {
            throw new DataValidException("You are not author of this album");
        }
    }

    private Album getAlbumOrThrowException(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
    }

    private List<AlbumDtoResponse> getFiltersAlbumDtos(AlbumFilterDto albumFilterDto, Stream<Album> albumStream) {
        List<AlbumFilter> filters = albumFilters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .toList();
        for (AlbumFilter filter : filters) {
            albumStream = filter.apply(albumStream, albumFilterDto);
        }

        return albumStream.map(this::visibilityFiltration)
                .filter(Objects::nonNull)
                .map(this::processAlbumToDto)
                .toList();
    }

    private Album visibilityFiltration(Album album) {
        long userId = userContext.getUserId();
        AlbumVisibility visibility = album.getVisibility();
        long authorId = album.getAuthorId();

        if (visibility.equals(AlbumVisibility.ONLY_AUTHOR)) {
            if (authorId != userId) {
                album = null;
            }
        }

        if (visibility.equals(AlbumVisibility.ONLY_SUBSCRIBERS)) {
            List<Long> followerIds = userServiceClient.getUser(authorId).getFollowerIds();
            if (!followerIds.contains(userId) && authorId != userId) {
                album = null;
            }
        }

        if (visibility.equals(AlbumVisibility.ONLY_SELECTED_BY_AUTHOR)) {
            List<Long> usersWithAccessIds = album.getUsersWithAccessIds();
            if (!usersWithAccessIds.contains(userId) && authorId != userId) {
                album = null;
            }
        }

        return album;
    }

    private AlbumDtoResponse processAlbumToDto(Album album) {
        long userId = userContext.getUserId();
        AlbumDtoResponse albumDtoResponse;

        if (album.getAuthorId() == userId) {
            albumDtoResponse = albumMapper.toAuthorAlbumDto(album);
        } else {
            albumDtoResponse = albumMapper.toAlbumDto(album);
        }

        return albumDtoResponse;
    }
}