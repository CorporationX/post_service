package faang.school.postservice.dto.Post;

import faang.school.postservice.dto.comment.LastCommentDto;
import faang.school.postservice.dto.user.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostInfoDto {
    @Value("${news-feed.feed.max_comments}")
    private int maxComments;
    private String postContent;
    private UserInfoDto dto;
    private long likes;
    private long views;
    private LocalDateTime updatedAt;
    private LinkedHashSet<LastCommentDto> comments = new LinkedHashSet<>(maxComments);
}
