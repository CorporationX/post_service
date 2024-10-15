package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.user.UserProfilePic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedDto {
    private Long id;
    private String username;
    private UserProfilePic userProfilePic;
}
