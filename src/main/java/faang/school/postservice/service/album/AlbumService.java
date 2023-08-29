package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final AlbumMapper albumMapper;

    @Transactional
    public AlbumDto createAlbum(AlbumCreateDto albumCreateDto) {
        if (userServiceClient.getUser(albumCreateDto.getAuthorId()) == null) {
            throw new DataValidException("User not found");
        }
        if (albumRepository.existsByTitleAndAuthorId(albumCreateDto.getTitle(), albumCreateDto.getAuthorId())) {
            throw new IllegalArgumentException("Album already exists");
        }
        return albumMapper.toAlbumDto(albumRepository.save(albumMapper.toAlbumCreate(albumCreateDto)));
    }
}
