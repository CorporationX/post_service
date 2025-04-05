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
    private final AlbumMapper albumMapper;
    private final AlbumValidator albumValidator;
    private final UserServiceClient userService;
    private final UserContext userContext;
    private final UserExistValidator userExistValidator;
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final AlbumFilterService albumFilterService;

    @Override
    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        Album album = albumMapper.toAlbum(albumDto);

        albumValidator.validateUniqueTitle(album);
        userExistValidator.userExist(userContext.getUserId());

        final Album savedAlbum = albumRepository.save(album);
        return albumMapper.toAlbumDto(savedAlbum);
    }

    @Override
    @Transactional
    public AlbumDto addPostToAlbum(long albumId, long postId, long userId) {
        Album album = albumRepository.findById(albumId);

        Post post = postRepositoryAdapter.findById(postId);

        albumValidator.validateAddPostToAlbum(album, postId, userId);

        album.addPost(post);
        albumRepository.save(album);
        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public AlbumDto removePostFromAlbum(long albumId, long postId, long userId) {
        Album album = albumRepository.findById(albumId);

        albumValidator.validateRemovePostFromAlbum(album, postId, userId);

        album.removePost(postId);

        albumRepository.save(album);

        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public AlbumDto addAlbumToFavorite(long albumId, long userId) {
        Album album = albumRepository.findById(albumId);

        albumValidator.validateAddAlbumToFavorite(album, userId);

        albumRepository.addAlbumToFavorites(albumId, userId);

        albumRepository.save(album);

        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public AlbumDto removeAlbumFromFavorite(long albumId, long userId) {
        Album album = albumRepository.findById(albumId);

        albumValidator.validateRemoveAlbumFromFavorite(album, userId);

        albumRepository.deleteAlbumFromFavorites(albumId, userId);

        albumRepository.save(album);

        return albumMapper.toAlbumDto(album);
    }

    //
    @Override
    @Transactional
    public AlbumDto getAlbumById(long albumId) {

        Album album = albumRepository.findById(albumId);

        return albumMapper.toAlbumDto(album);
    }

    @Override
    @Transactional
    public List<AlbumDto> getAllUserAlbums(long userId, AlbumFilterDto albumFilterDto) {
        return albumFilterService.applyFilters(albumRepository.findByAuthorId(userId), albumFilterDto)
                .map(albumMapper::toAlbumDto)
                .toList();
    }

//    @Override
//    @Transactional
//    public List<AlbumDto> getAllAlbums(AlbumFilterDto albumFilterDto) {
//        return albumFilterService.applyFilters(albumRepository.findAll().stream(), albumFilterDto)
//                .map(albumMapper::toAlbumDto)
//                .toList();
//    }
//
//    @Override
//    @Transactional
//    public List<AlbumDto> getAllUserFavoriteAlbums(long userId, AlbumFilterDto albumFilterDto) {
//        return albumFilterService.applyFilters(albumRepository.findFavoriteAlbumsByUserId(userId), albumFilterDto)
//                .map(albumMapper::toAlbumDto)
//                .toList();
//    }
//
//    @Override
//    @Transactional
//    public AlbumDto updateAlbum(long albumId, long userId, AlbumDto albumDto) {
//
//        Album albumToUpdate = albumRepository.findById(albumId);
//
//        albumValidator.validateUpdateAlbum(albumToUpdate, userId, albumDto);

//        albumRepository.update(albumDto, albumToUpdate);

//        return albumMapper.toAlbumDto(albumRepository.save(albumToUpdate));
//    }
//
//    @Override
//    @Transactional
//    public AlbumDto deleteAlbum(long albumId, long userId) {
//
//        Album album = albumRepository.findById(albumId);
//        AlbumDto albumDto = albumMapper.toAlbumDto(album);
//
//        albumValidator.validateDeleteAlbum(album, userId);
//
//        albumRepository.deleteById(albumId);
//        return albumDto;
//    }
}
