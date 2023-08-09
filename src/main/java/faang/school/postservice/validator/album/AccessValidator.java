package faang.school.postservice.validator.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotAllowedException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Visibility;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessValidator {
    private final ObjectMapper objectMapper;
    private final UserServiceClient userService;

    public void validateUpdateAccess(AlbumDto dto, long userId) {
        if (userId != dto.getAuthorId()) throw new NotAllowedException("Only author can change the album");
    }

    public void validateUpdateAccess(Album album, long userId) {
        if (userId != album.getAuthorId()) throw new NotAllowedException("Only author can change the album");
    }

    public void validateAccess(Album album, long userId) throws JsonProcessingException {
        if (userId == album.getAuthorId()) return;
        if (album.getVisibility() == Visibility.EVERYONE) return;
        if (album.getVisibility() == Visibility.ONLY_ME && userId == album.getAuthorId()) return;

        var users = userService.getUsersByIds(objectMapper.readValue(album.getAllowedUsersIds(), new TypeReference<>() {
                }))
                .stream()
                .map(UserDto::id)
                .collect(Collectors.toSet());

        if (album.getVisibility() == Visibility.SPECIFIC_USERS && users.contains(userId)) return;
        try {
            boolean isFollower = userService.getFollowing(album.getAuthorId())
                    .stream()
                    .map(UserDto::id)
                    .toList()
                    .contains(userId);

            if (userId == album.getAuthorId()
                    || (album.getVisibility() == Visibility.FOLLOWERS && isFollower)) return;
        } catch (FeignException.FeignClientException e) {
            log.error("FeignException: " + e.getMessage() + '\n' + Arrays.toString(e.getStackTrace()));
        }
        throw new NotAllowedException(String.format("User with id=%d don't have access to the album with id=%d", userId, album.getId()));
    }
}
