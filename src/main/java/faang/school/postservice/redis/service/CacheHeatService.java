package faang.school.postservice.redis.service;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.UserDto;

import java.util.List;

public interface CacheHeatService {

    void heatCache(long startUserId, long endUserId);

    void populateCache(Long followerId, List<UserDto> bloggers, List<PostDto> posts);
}
