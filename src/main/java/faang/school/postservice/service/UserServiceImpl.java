package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.cache.MultiGetCacheService;
import faang.school.postservice.service.cache.SingleCacheService;
import faang.school.postservice.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserServiceClient userServiceClient;
    private final SingleCacheService<Long, UserDto> userCacheService;
    private final MultiGetCacheService<List<Long>, UserDto> userMultiGetCacheService;
    private final CollectionUtils collectionUtils;

    @Override
    public UserDto getUserFromCacheOrService(Long userId) {
        UserDto user = userCacheService.get(userId);
        return Objects.requireNonNullElseGet(user, () -> {
            log.warn("User with id {} not found", userId);
            return userServiceClient.getUser(userId);
        });
    }

    @Override
    public List<UserDto> getUsersFromCacheOrService(List<Long> userIds) {
        List<UserDto> users = userMultiGetCacheService.getAll(userIds);
        List<Long> missingUserIds = getMissingUserIds(userIds, users);

        if (!missingUserIds.isEmpty()) {
            List<UserDto> missingUsers = userServiceClient.getUsersByIds(missingUserIds);
            collectionUtils.replaceNullsWith(users, missingUsers);
        }

        return users;
    }

    private List<Long> getMissingUserIds(List<Long> userIds, List<UserDto> users) {
        return IntStream.range(0, users.size())
                .filter(i -> users.get(i) == null)
                .mapToObj(userIds::get)
                .toList();
    }
}
