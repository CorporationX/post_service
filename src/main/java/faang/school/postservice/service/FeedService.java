package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    @Value("${feed.batch}")
    private int feedBatch;

    public List<Long> getNext20PostIds(LinkedHashSet<Long> postIds, @Nullable Long afterId) {
        if (afterId == null) {
            return postIds.stream().limit(feedBatch).toList();
        }
        Iterator<Long> it = postIds.iterator();
        List<Long> next20Posts = new ArrayList<>();
        boolean isFound = false;
        while (it.hasNext() && next20Posts.size() < feedBatch) {
            Long currentPostId = it.next();
            if (isFound || currentPostId.equals(afterId)) {
                isFound = true;
                next20Posts.add(currentPostId);
            }
        }
        return next20Posts;
    }
}
