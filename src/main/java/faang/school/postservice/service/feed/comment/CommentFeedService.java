package faang.school.postservice.service.feed.comment;

import faang.school.postservice.dto.feed.CommentFeedDto;

import java.util.List;

public interface CommentFeedService {

    List<CommentFeedDto> getCommentsWithAuthors(long postId);
}
