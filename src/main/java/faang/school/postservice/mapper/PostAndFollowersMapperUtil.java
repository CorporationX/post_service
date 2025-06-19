package faang.school.postservice.mapper;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Named("PostAndFollowersMapperUtil")
@Component
@RequiredArgsConstructor
public class PostAndFollowersMapperUtil {
    private final UserServiceClient userServiceClient;

    @Named("getFollowers")
    public List<Long> getFollowers(Long userId) {
        return userServiceClient.getFollowers(userId);
    }
}
