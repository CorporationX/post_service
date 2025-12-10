package faang.school.postservice.integration.user.service;

import faang.school.postservice.integration.user.dto.UserResponseDto;

public interface UserServiceClient {
    UserResponseDto getUser(long id);
}
