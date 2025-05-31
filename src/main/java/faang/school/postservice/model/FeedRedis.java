package faang.school.postservice.model;

import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.LinkedList;

@Getter
public class FeedRedis {
    public final static int INITIAL_VERSION = 1;

    private final long userId;
    private final LinkedHashSet<Long> latestPostIds = new LinkedHashSet<>();
    private int version = INITIAL_VERSION;

    private FeedRedis(long userId) {
        this.userId = userId;
    }

    public static FeedRedis createNew(long userId, long postId) {
        var result = new FeedRedis(userId);
        result.getLatestPostIds().add(postId);

        return result;
    }

    public void addPostId(long postId, int maxFeedSize) {
        trunkPostIdsToMaxSize(maxFeedSize);
        latestPostIds.add(postId);
    }

    public void updateVersion() {
        version++;
    }

    private void trunkPostIdsToMaxSize(int maxFeedSize) {
        var linkedList = new LinkedList<>(latestPostIds);
        var reverseIterator = linkedList.descendingIterator();
        while (latestPostIds.size() >= maxFeedSize) {
            latestPostIds.remove(reverseIterator.next());
        }
    }
}
