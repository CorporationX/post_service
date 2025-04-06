package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.PostAlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostAlbumMapper;
import faang.school.postservice.repository.PostAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostAlbumIServiceImpl implements PostAlbumService {
    private final PostAlbumRepository postAlbumRepository;
    private final PostAlbumMapper postAlbumMapper;
    private final UserServiceClient userServiceClient;
    private final AlbumService albumService;

    @Override
    public PostAlbumDto addPostToAlbum(PostAlbumDto dto) {
        UserDto userDto = userServiceClient.getUser(dto.userId());
        if (userDto == null) {
            throw new DataValidationException("The user is not found");
        }
        if (!albumService.existsById(dto.albumId())) {
            throw new DataValidationException("The album is not found");
        }
        if (userDto.id() != dto.albumId()) {
            throw new DataValidationException("The user is not the album owner");
        }
        return postAlbumMapper.toPostAlbumDto(postAlbumRepository.save(postAlbumMapper.toPostAlbum(dto)));
    }
}
