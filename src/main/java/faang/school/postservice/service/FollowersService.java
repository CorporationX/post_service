package faang.school.postservice.service;

import java.util.List;

public interface FollowersService {
    List<Long> getFollowerIds(Long authorId);
}
