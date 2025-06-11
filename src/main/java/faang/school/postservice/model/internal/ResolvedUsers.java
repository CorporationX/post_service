package faang.school.postservice.model.internal;

import faang.school.postservice.dto.user.UserDto;

import java.util.Map;

public record ResolvedUsers(
        Map<Long, UserDto> userDtos,
        boolean partial
) {}