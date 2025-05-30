package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AlbumService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumMapper albumMapper;
    private final List<AlbumFilter> albumFilters;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public AlbumUpdateDto createAlbum(AlbumCreateDto albumCreateDto) {
        checkUniqueTittle(albumCreateDto.getTitle(), albumCreateDto.getAuthorId());

        Album createdAlbum = Album.builder()
                .authorId(albumCreateDto.getAuthorId())
                .title(albumCreateDto.getTitle())
                .description(albumCreateDto.getDescription())
                .posts(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        createdAlbum = albumRepository.save(createdAlbum);

        return albumMapper.toDto(createdAlbum);
    }

    @Override
    @Transactional
    public AlbumUpdateDto updateAlbum(Long albumId, AlbumUpdateDto albumUpdateDto) {
        Album albumToUpdate = albumRepository.findByIdWithPosts(albumUpdateDto.getAlbumId())
                .orElseThrow(EntityNotFoundException::new);
        if (checkAlbumAuthor(albumUpdateDto.getAuthorId(), albumToUpdate)) {
            throw new DataValidationException("Not able to update other's user album");
        }

        String newDescription = albumUpdateDto.getDescription();
        String newTittle = albumUpdateDto.getTitle();
        if (newDescription != null) {
            albumToUpdate.setDescription(newDescription);
            albumToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        if (newTittle != null) {
            albumToUpdate.setTitle(newTittle);
            albumToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        Album updatedAlbum = albumRepository.save(albumToUpdate);

        return albumMapper.toDto(updatedAlbum);
    }

    @Override
    @Transactional
    public void deleteAlbum(Long albumId, Long userId) {
        Album albumToDelete = albumRepository.findById(albumId)
                .orElseThrow(EntityNotFoundException::new);

        if (checkAlbumAuthor(userId, albumToDelete)) {
            throw new DataValidationException("Not able to delete other's user album");
        }
        albumRepository.delete(albumToDelete);
    }

    @Override
    @Transactional
    public AlbumUpdateDto addPostsToAlbum(Long albumId, AlbumUpdateDto albumUpdateDto) {
        Album albumToPostsAdd = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(EntityNotFoundException::new);
        if (checkAlbumAuthor(albumUpdateDto.getAuthorId(), albumToPostsAdd)) {
            throw new DataValidationException("Posts cannot be added to other user's album.");
        }
        checkPostsExist(albumUpdateDto.getPostsIds());

        List<Post> posts = (List<Post>) postRepository.findAllById(albumUpdateDto.getPostsIds());
        for (Post post : posts) {
            albumToPostsAdd.addPost(post);
        }
        albumRepository.save(albumToPostsAdd);

        return albumMapper.toDto(albumToPostsAdd);
    }


    @Override
    @Transactional
    public void deletePostsFromAlbum(Long albumId, AlbumUpdateDto albumUpdateDto) {
        Album albumToPostsDelete = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(EntityNotFoundException::new);
        if (checkAlbumAuthor(albumUpdateDto.getAuthorId(), albumToPostsDelete)) {
            throw new DataValidationException("Posts cannot be deleted from other user's album.");
        }
        checkPostsExist(albumUpdateDto.getPostsIds());

        List<Post> posts = (List<Post>) postRepository.findAllById(albumUpdateDto.getPostsIds());
        for (Post post : posts) {
            albumToPostsDelete.removePost(post.getId());
        }
        albumRepository.save(albumToPostsDelete);
    }

    @Override
    @Transactional
    public void addAlbumToFavorites(Long userId, Long albumId) {
        findUserById(userId);
        Album album = albumRepository.findByIdWithPosts(albumId)
                .orElseThrow(EntityNotFoundException::new);
        if (checkAlbumAuthor(userId, album)) {
            throw new DataValidationException("Not able to add album to other's user favorites");
        }
        albumRepository.addAlbumToFavorites(album.getId(), userId);
    }

    @Override
    @Transactional
    public void deleteAlbumFromFavorites(Long userId, Long albumId) {
        findUserById(userId);
        List<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        if (albums.isEmpty()) {
            throw new EntityNotFoundException("Albums not found");
        }
        for (Album album : albums) {
            if (checkAlbumAuthor(userId, album)) {
                throw new DataValidationException("Not able to delete album from other's user favorites");
            }

            if (album.getId() == albumId) {
                albumRepository.deleteAlbumFromFavorites(album.getId(), userId);
            }
        }
    }

    @Override
    public AlbumUpdateDto getAlbumById(Long albumId) {
        Album album = albumRepository.findByIdWithPosts(albumId).orElseThrow(EntityNotFoundException::new);

        return albumMapper.toDto(album);
    }

    @Override
    public List<AlbumUpdateDto> getUserAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto) {
        findUserById(userId);
        List<Album> albums = albumRepository.findByAuthorId(userId);
        Stream<AlbumUpdateDto> albumStream = albumMapper.toDtoList(albums).stream();
        for (AlbumFilter filter : albumFilters) {
            if (filter.isApplicable(albumFilterDto)) {
                albumStream = filter.apply(albumStream, albumFilterDto);
            }
        }

        return albumStream.toList();
    }

    @Override
    public List<AlbumUpdateDto> getAllAlbumsWithFilters(AlbumFilterDto albumFilterDto) {
        List<Album> albums = (List<Album>) albumRepository.findAll();
        Stream<AlbumUpdateDto> albumStream = albumMapper.toDtoList(albums).stream();
        for (AlbumFilter filter : albumFilters) {
            if (filter.isApplicable(albumFilterDto)) {
                albumStream = filter.apply(albumStream, albumFilterDto);
            }
        }

        return albumStream.toList();
    }

    @Override
    public List<AlbumUpdateDto> getAllFavoritesUserAlbumsWithFilters(Long userId, AlbumFilterDto albumFilterDto) {
        findUserById(userId);
        List<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        Stream<AlbumUpdateDto> albumStream = albumMapper.toDtoList(albums).stream();

        for (AlbumFilter filter : albumFilters) {
            if (filter.isApplicable(albumFilterDto)) {
                albumStream = filter.apply(albumStream, albumFilterDto);
            }
        }

        return albumStream.toList();
    }


    private void checkUniqueTittle(String tittle, long authorId) {
        if (albumRepository.existsByTitleAndAuthorId(tittle, authorId)) {
            throw new DataValidationException("Album tittle must be unique.");
        }
    }

    private boolean checkAlbumAuthor(Long authorId, Album album) {
        return !authorId.equals(album.getAuthorId());
    }

    private void checkPostsExist(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            throw new DataValidationException("Posts not found.");
        }
    }

    private UserDto findUserById(long userId) {
        return userServiceClient.getUser(userId);
    }
}