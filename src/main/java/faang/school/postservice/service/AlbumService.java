package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    @Transactional(readOnly = true)
    public Album createAlbum(Album album) {
        validUserExist(album.getAuthorId());
        validUniqueAlbumTitleByAuthor(album);
        return albumRepository.save(album);
    }

    @Transactional
    public void addAlbumToFavorite(long albumId, long authorId) {
        validUserExist(authorId);
        validAlbumBelongsToUser(albumId, authorId);
        albumRepository.addAlbumToFavorites(albumId, authorId);
    }

    @Transactional
    public void removeAlbumToFavorite(long albumId, long authorId) {
        validUserExist(authorId);
        validAlbumBelongsToUser(albumId, authorId);
        albumRepository.deleteAlbumFromFavorites(albumId, authorId);
    }

    private void validUserExist(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        if (Objects.isNull(userDto)) {
            throw new IllegalArgumentException("This user does not exist.");
        }
    }

    private void validAlbumBelongsToUser(long albumId, long authorId) {
        List<Long> authorAlbumIds = albumRepository.findByAuthorId(authorId)
                .map(Album::getId)
                .toList();

        if(!authorAlbumIds.contains(albumId)) {
            throw new IllegalArgumentException("The album don`t belong the user.");
        }
    }


        private void validUniqueAlbumTitleByAuthor(Album album) {
        boolean uniqueAlbumTitle = albumRepository
                .findByAuthorId(album.getAuthorId())
                .noneMatch(existingAlbum -> existingAlbum.getTitle().equals(album.getTitle()));
        if (!uniqueAlbumTitle) {
            throw new IllegalArgumentException("The album name must be unique for this user.");
        }
    }
}