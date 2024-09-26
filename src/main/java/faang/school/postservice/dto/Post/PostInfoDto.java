package faang.school.postservice.dto.Post;

import faang.school.postservice.dto.comment.LastCommentDto;
import faang.school.postservice.dto.user.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostInfoDto {
    private String postContent;
    private UserInfoDto dto;
    private long likes;
    private LocalDateTime updatedAt;
    private LinkedHashSet<LastCommentDto> comments;
}
