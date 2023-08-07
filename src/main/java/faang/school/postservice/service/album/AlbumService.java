package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.album.AlbumDataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final UserServiceClient userServiceClient;

    @Transactional
    public AlbumDto createAlbum(AlbumDto albumDto) {
        checkIfAuthorExists(albumDto);
        checkIfAlbumHasUniqueTitle(albumDto);

        Album album = albumMapper.toEntity(albumDto);
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());

        log.info("Created album: {}", album);
        return albumMapper.toDto(albumRepository.save(album));
    }

    private void checkIfAuthorExists(AlbumDto albumDto) {
        UserDto user = userServiceClient.getUser(albumDto.getAuthorId());
        if (user == null) {
            throw new AlbumDataValidationException("There is no user with id " + albumDto.getAuthorId());
        }
    }

    private void checkIfAlbumHasUniqueTitle(AlbumDto albumDto) {
        albumRepository.findByAuthorId(albumDto.getAuthorId())
                .forEach(album -> {
                    if (album.getTitle().equals(albumDto.getTitle())) {
                        throw new AlbumDataValidationException("Title of the album should be unique");
                    }
                });
    }
}