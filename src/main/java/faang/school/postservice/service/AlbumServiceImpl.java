package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.filter.albumvisibility.AlbumVisibilityFilter;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@Qualifier("albumServiceImpl")
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserContext userContext;
    private final Map<AlbumVisibility, AlbumVisibilityFilter> albumVisibilities;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, UserContext userContext, List<AlbumVisibilityFilter> filters) {
        this.albumRepository = albumRepository;
        this.userContext = userContext;
        this.albumVisibilities = filters.stream()
                .collect(toMap(AlbumVisibilityFilter::getAlbumVisibility, Function.identity()));
    }

    @Override
    public AlbumResponseDto getAlbumById(long id) {
        Album album = albumRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", id)));
        return albumVisibilities.get(album.getAlbumVisibility()).apply(album);
    }

    @Override
    public List<AlbumResponseDto> getAlbumsByAuthorId(long authorId) {
        return albumRepository.findByAuthorId(authorId).stream()
                .map(album -> albumVisibilities.get(album.getAlbumVisibility()).apply(album))
                .toList();
    }

    @Transactional
    @Override
    public AlbumResponseDto updateAlbumVisibility(long id, AlbumVisibility albumVisibility) {
        Album album = albumRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", id)));
        long userId = userContext.getUserId();
        validateAuthor(userId, album);
        album.setAlbumVisibility(albumVisibility);
        Album savedAlbum = albumRepository.save(album);
        return albumVisibilities.get(savedAlbum.getAlbumVisibility()).apply(savedAlbum);
    }

    @Transactional
    @Override
    public List<Long> addUsersForAccessAlbum(long albumId, AlbumUsersDto albumUsersDto) {
        Album album = albumRepository.findById(albumId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", albumId)));
        long userId = userContext.getUserId();
        validateAuthor(userId, album);
        checkVisibilityForAlbum(album);
        albumUsersDto.usersIds().forEach(id -> albumRepository.addUserForVisibilityAtAlbum(album.getId(), id));
        return albumUsersDto.usersIds();
    }

    private void validateAuthor(long userId, Album album) {
        if (userId != album.getAuthorId()) {
            log.error("User with id = {} isn't author for album with id = {}", userId, album.getId());
            throw new AlbumAccessDeniedException(
                    String.format("User with id = %d isn't author for album with id = %d", userId, album.getId()));
        }
    }

    private void checkVisibilityForAlbum(Album album) {
        if (!AlbumVisibility.SELECTED.equals(album.getAlbumVisibility())) {
            log.error("Visibility isn't {} in album with id = {}", AlbumVisibility.SELECTED, album.getId());
            throw new IllegalArgumentException(
                    String.format("Needed selected_users visibility for add users for access. Album: %d", album.getId()));
        }
    }
}
