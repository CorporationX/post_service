package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import org.springframework.data.domain.Page;

public interface FeedService {
    Page<FeedPostDto> getFeedPosts(Long userId, int offset);

    Page<FeedCommentDto> getFeedComments(Long userId, int offset);

    void heatFeedCache();
}
