package faang.school.postservice.service.follow;

import faang.school.postservice.client.FollowFeignClient;
import faang.school.postservice.dto.user.follower.FollowersPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {

    private final FollowFeignClient client;

    @Override
    public List<Long> getAllFollowingAuthorIds(long userId) {
        List<Long> result = new ArrayList<>();
        FollowersPage page = client.getFollowingIds(userId, null, 1000);
        while (page != null && page.ids() != null && !page.ids().isEmpty()) {
            result.addAll(page.ids());
            String nextCursor = page.nextCursor();
            if (nextCursor == null) {
                break;
            }
            page = client.getFollowingIds(userId, nextCursor, 1000);
        }
        return result;
    }
}
