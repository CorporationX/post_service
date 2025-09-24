package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.user.feed.FeedAuthorDto;

import java.time.Instant;
import java.util.List;

public record FeedPostDto(
        Long id,
        String content,
        Instant publishedAt,
        FeedAuthorDto author,
        List<FeedCommentDto> lastComments
) {}