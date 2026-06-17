package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.filter.service.AlbumFilterService;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.AlbumRepositoryAdapter;
import faang.school.postservice.repository.PostRepositoryAdapter;
import faang.school.postservice.service.AlbumService;
import faang.school.postservice.validator.AlbumValidator;
import faang.school.postservice.validator.UserExistValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumRepositoryAdapter albumRepositoryAdapter;
    private final AlbumMapper albumMapper;
    private final AlbumValidator albumValidator;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final UserExistValidator userExistValidator;
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final AlbumFilterService albumFilterService;

    @Override
    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        Album album = albumMapper.toAlbum(albumDto);
        var userId = userContext.getUserId();

        album.setAuthorId(userId);
        userExistValidator.userExist(userId);
        albumValidator.validateUniqueTitle(album);

        return albumMapper.toAlbumDto(albumRepository.save(album));
    }

    @Override
    @Transactional
    public AlbumDto addPostToAlbum(long albumId, long postId) {
        Album album = albumRepositoryAdapter.findById(albumId);

        Post post = postRepositoryAdapter.findById(postId);

        albumValidator.validateAddPostToAlbum(album, postId, userContext.getUserId());

        album.addPost(post);
        albumRepository.save(album);
        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public AlbumDto removePostFromAlbum(long albumId, long postId) {
        Album album = albumRepositoryAdapter.findById(albumId);

        albumValidator.validateRemovePostFromAlbum(album, postId, userContext.getUserId());

        album.removePost(postId);

        albumRepository.save(album);

        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public AlbumDto addAlbumToFavorite(long albumId) {
        Album album = albumRepositoryAdapter.findById(albumId);

        albumValidator.validateAddAlbumToFavorite(album, userContext.getUserId());

        albumRepository.addAlbumToFavorites(albumId, userContext.getUserId());

        albumRepository.save(album);

        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public AlbumDto removeAlbumFromFavorite(long albumId) {
        Album album = albumRepositoryAdapter.findById(albumId);

        albumValidator.validateRemoveAlbumFromFavorite(album, userContext.getUserId());

        albumRepository.deleteAlbumFromFavorites(albumId, userContext.getUserId());

        albumRepository.save(album);

        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public AlbumDto getAlbumById(long albumId) {

        Album album = albumRepositoryAdapter.findById(albumId);

        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public List<AlbumDto> getAllUserAlbums(AlbumFilterDto albumFilterDto) {
        return albumFilterService
                .applyFilters(albumRepository.findByAuthorId(userContext.getUserId()), albumFilterDto)
                .map(albumMapper::toAlbumDto)
                .toList();
    }

    @Override
    @Transactional
    public List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto) {
        return albumFilterService.applyFilters(albumRepository.findAll().stream(), albumFilterDto)
                .map(albumMapper::toAlbumDto)
                .toList();
    }

    @Override
    @Transactional
    public List<AlbumDto> getAllUserFavoriteAlbums(AlbumFilterDto albumFilterDto) {
        return albumFilterService
                .applyFilters(albumRepository
                        .findFavoriteAlbumsByAuthorId(userContext.getUserId()), albumFilterDto)
                .map(albumMapper::toAlbumDto)
                .toList();
    }

    @Override
    @Transactional
    public AlbumDto updateAlbum(long albumId, AlbumDto albumDto) {

        Album albumToUpdate = albumRepositoryAdapter.findById(albumId);

        albumValidator.validateUpdateAlbum(albumToUpdate, userContext.getUserId(), albumDto);

        albumMapper.update(albumDto, albumToUpdate);

        Album updatedAlbum = albumRepository.save(albumToUpdate);

        return albumMapper.toAlbumDto(updatedAlbum);
    }

    @Override
    @Transactional
    public AlbumDto deleteAlbum(long albumId) {

        Album album = albumRepositoryAdapter.findById(albumId);
        AlbumDto albumDto = albumMapper.toAlbumDto(album);

        albumValidator.validateDeleteAlbum(album, userContext.getUserId());

        albumRepository.deleteById(albumId);
        return albumDto;
    }
}
