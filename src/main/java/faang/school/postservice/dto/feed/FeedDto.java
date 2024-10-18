package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.dto.user.UserFeedDto;

import java.time.LocalDateTime;
import java.util.List;

public class FeedDto {
  private Long id;
  private String content;
  private UserFeedDto author;
  private Long projectId;
  private List<Long> likeIds;
  private Long likeCount;
  private List<CommentFeedDto> comments;
  private List<Long> albumIds;
  private Long adId;
  private List<Long> resourceIds;
  private boolean published;
  private LocalDateTime publishedAt;
  private LocalDateTime scheduledAt;
  private boolean deleted;
  private Long numLikes;
}
