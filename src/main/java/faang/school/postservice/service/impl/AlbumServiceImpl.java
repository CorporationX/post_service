package faang.school.postservice.service.impl;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.dto.AlbumDto;
import faang.school.postservice.model.dto.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.entity.Album;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AlbumService;
import faang.school.postservice.specification.AlbumSpecificationBuilder;
import faang.school.postservice.validator.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final UserContext userContext;
    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final List<AlbumFilter> albumFilters;
    private final AlbumMapper albumMapper;
    private final AlbumValidator validator;

    @Override
    @Transactional
    public AlbumDto create(AlbumDto albumDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        validator.validateAlbumNotExists(albumDto.getTitle(), userId);

        Album album = Album.builder()
                .title(albumDto.getTitle())
                .description(albumDto.getDescription())
                .authorId(userId)
                .build();
        return albumMapper.albumToAlbumDto(albumRepository.save(album));
    }

    @Override
    @Transactional
    public String   addPostToAlbum(Long albumId, Long postId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        if (postRepository.existsInAlbum(albumId, postId)) {
            return String.format("Post id = %d already exists in album id = %d", postId, albumId);
        }
        Album album = findAlbumById(albumId);
        Post post = findPostById(postId);
        album.addPost(post);
        albumRepository.save(album);
        return String.format("Post id = %d added in album id = %d, title = %s", postId, albumId, album.getTitle());
    }

    @Override
    @Transactional
    public AlbumDto deletePostFromAlbum(Long albumId, Long postId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Album album = findAlbumById(albumId);
        Post post = findPostById(postId);
        album.removePost(post.getId());
        return albumMapper.albumToAlbumDto(albumRepository.save(album));
    }

    @Override
    @Transactional
    public String addAlbumToFavorites(Long albumId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        if (albumRepository.existsInFavorites(albumId, userId)) {
            return String.format("Album with id = %d for user id = %d already exists in favorites", albumId, userId);
        }
        Album album = findAlbumById(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
        return String.format("Album with id = %d, title = %s added to favorites", albumId, album.getTitle());
    }

    @Override
    @Transactional
    public String deleteAlbumFromFavorites(Long albumId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Album album = findAlbumById(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
        return String.format("Album with id = %d, title = %s deleted from favorites", albumId, album.getTitle());
    }

    @Override
    public AlbumDto getAlbumById(Long albumId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Album album = findAlbumById(albumId);
        return albumMapper.albumToAlbumDto(album);
    }

    @Override
    public List<AlbumDto> getUserAlbums(AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        albumFilterDto.setAuthorId(userId);
        Specification<Album> spec = AlbumSpecificationBuilder.buildSpecification(albumFilterDto);
        return albumMapper.toDtoList(albumRepository.findAll(spec));
    }

    @Override
    public List<AlbumDto> getAllUsersAlbums(AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        albumFilterDto.setAuthorId(null);
        Specification<Album> spec = AlbumSpecificationBuilder.buildSpecification(albumFilterDto);
        return albumMapper.toDtoList(albumRepository.findAll(spec));
    }

    @Override
    public List<AlbumDto> getUserFavoriteAlbums(AlbumFilterDto albumFilterDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Stream<Album> favoriteAlbumsStream = albumRepository.findFavoriteAlbumsByUserId(userId);
        List<Album> filteredFavoriteAlbums = albumFilters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(favoriteAlbumsStream,
                        (stream, albumFilter) -> albumFilter.apply(stream, albumFilterDto),
                        (s1, s2) -> s1)
                .toList();
        return albumMapper.toDtoList(filteredFavoriteAlbums);
    }

    @Override
    @Transactional
    public AlbumDto update(Long albumId, AlbumDto albumDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        validator.validateUserIsAuthor(userId, albumDto.getAuthorId());
        Album album = findAlbumById(albumId);
        album.setTitle(albumDto.getTitle());
        album.setDescription(albumDto.getDescription());
        updatePostsIfNeeded(albumDto.getPostIds(), album);
        return albumMapper.albumToAlbumDto(albumRepository.save(album));
    }

    @Override
    @Transactional
    public void delete(Long albumId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Album album = findAlbumById(albumId);
        validator.validateUserIsAuthor(userId, album.getAuthorId());
        albumRepository.delete(album);
    }

    private void updatePostsIfNeeded(List<Long> dtoPostIds, Album album) {
        List<Post> posts = album.getPosts();

        Set<Long> albumPostIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toSet());

        Set<Long> postIdsForRemove = new HashSet<>(albumPostIds);
        dtoPostIds.forEach(postIdsForRemove::remove);
        postIdsForRemove.forEach(album::removePost);

        Set<Long> postIdsForAdd = new HashSet<>(dtoPostIds);
        albumPostIds.forEach(postIdsForAdd::remove);
        postRepository.findAllById(postIdsForAdd).forEach(album::addPost);
    }

    private Album findAlbumById(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new NoSuchElementException(
                String.format("Album with id = %d not found", albumId)));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException(
                String.format("Post with id = %d not found", postId)));
    }
}
