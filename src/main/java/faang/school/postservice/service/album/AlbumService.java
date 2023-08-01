package faang.school.postservice.service.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NotAllowedException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Visibility;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository repository;
    private final AlbumMapper mapper;
    private final ObjectMapper objectMapper;
    private final UserServiceClient userService;

    @Transactional
    public AlbumDto createAlbum(AlbumDto dto) throws JsonProcessingException {
        var album = mapper.toEntity(dto);
        album.setAllowedUsersIds(objectMapper.writeValueAsString(dto.getAllowedUsersIds()));
        var alDto = mapper.toDto(repository.save(album));
        alDto.setAllowedUsersIds(objectMapper.readValue(album.getAllowedUsersIds(), new TypeReference<>() {}));
        return alDto;
    }

    @Transactional
    public AlbumDto getAlbum(Long albumId, long userId) throws JsonProcessingException {
        var album = repository.findById(albumId).orElseThrow(() -> new EntityNotFoundException("Entity wasn`t found"));
        validateAccess(album, userId);

        var res = mapper.toDto(album);
        res.setAllowedUsersIds(objectMapper.readValue(album.getAllowedUsersIds(), new TypeReference<>() {}));
        return res;
    }

    private void validateAccess(Album album, long userId) throws JsonProcessingException {
        if (album.getVisibility() == Visibility.EVERYONE) return;
        if (album.getVisibility() == Visibility.ONLY_ME && userId == album.getAuthorId()) return;

        var users = userService.getUsersByIds(objectMapper.readValue(album.getAllowedUsersIds(), new TypeReference<>() {
                }))
                .stream()
                .map(UserDto::id)
                .collect(Collectors.toSet());

        if (album.getVisibility() == Visibility.SPECIFIC_USERS && users.contains(userId)) return;
        try {
            if (userId == album.getAuthorId() ||
                    (album.getVisibility() == Visibility.FOLLOWERS
                            && userService.getFollowing(album.getAuthorId()).stream().map(UserDto::id).toList().contains(userId)))
                return;
        } catch (Exception e) {} // тут может вылететь ошибка, что у юзера не фолловеров, но это логично не обрабатывать, наверное, просто забить

        throw new NotAllowedException("U don`t have access to the album");
    }
}
