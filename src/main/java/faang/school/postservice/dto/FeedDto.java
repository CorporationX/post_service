package faang.school.postservice.dto;

import faang.school.postservice.dto.Post.PostInfoDto;
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

public class FeedDto {
    private UserInfoDto authorInfo;
    private PostInfoDto postInfo;
//    private String postContent;
//    private LinkedHashSet<LastCommentDto> comments;
//    private long likes;
//    private LocalDateTime updatedAt;
}
