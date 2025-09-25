package faang.school.postservice.service.user;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.props.FeedProps;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.redis.UserRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final FeedProps feedProps;
    private final UserMapper userMapper;
    private final UserServiceClient userClient;
    private final ProjectServiceClient projectClient;

    public List<Long> getFollowees(Long userId, Long projectId) {
        if (userId != null) {
            log.info("Get followers for userId = {}", userId);
            return userClient.getFolloweeIds(userId);
        } else {
            log.info("Get followers for projectId = {}", projectId);
            return projectClient.getFolloweeIds(projectId);
        }
    }

    public List<Long> getSubs(Long userId) {
        return userClient.getSubsIds(userId);
    }

    public List<UserDto> getBatch(int batch) {
        return userClient.getUsers(batch);
    }

    public UserRedis toUserRedis(Long userId, Long projectId) {
        UserRedis userRedis;
        try {
            if (userId != null) {
                UserDto user = getUserById(userId);
                userRedis = userMapper.toUserRedis(user);
            } else {
                ProjectDto project = getProjectById(projectId);
                userRedis = userMapper.toUserRedis(project);
            }

            userRedis.setTtl(feedProps.user().ttl());
            return userRedis;
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to get author. Cause:", e.getMessage());
        }
    }

    private UserDto getUserById(long userId) {
        log.info("Get user from user-service by userId = {}", userId);
        return userClient.getUser(userId);
    }

    private ProjectDto getProjectById(long projectId) {
        log.info("Get author from project-service by userId = {}", projectId);
        return projectClient.getProject(projectId);
    }
}
