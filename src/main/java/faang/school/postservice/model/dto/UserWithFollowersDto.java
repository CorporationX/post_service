package faang.school.postservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithFollowersDto {
    private Long userId;
    private String username;
    private String fileId;
    private String smallFileId;
    private LocalDateTime postCreatedAt;
    private List<Long> followerIds;
}
