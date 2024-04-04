package faang.school.postservice.service.hash;

import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.hash.FeedHash;
import faang.school.postservice.repository.FeedHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedHashServiceImpl implements FeedHashService{
    private final FeedHashRepository feedHashRepository;
    @Value("${feed.size}")
    private int feedSize;

    @Override
    public void updateFeed(PostEventKafka postEvent) {
        List<Long> followerIds = postEvent.getFollowerIds();
        followerIds.forEach((followerId) -> {
            FeedHash feedHash = feedHashRepository.findByUserId(followerId)
                    .orElseGet(() -> new FeedHash(followerId, new LinkedHashSet<>(), 1L));
            feedHash.getPostIds().add(postEvent.getPostId());

            while (feedHash.getPostIds().size() > feedSize) {
                Iterator<Long> iterator = feedHash.getPostIds().iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                } else {
                    break;
                }
            }

            feedHashRepository.save(feedHash);
        });
    }

}
