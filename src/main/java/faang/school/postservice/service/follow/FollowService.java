package faang.school.postservice.service.follow;

import java.util.List;

public interface FollowService {
    List<Long> getAllFollowingAuthorIds(long userId);
}
