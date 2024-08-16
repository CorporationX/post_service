package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfilePicDto {
    private String fileId;
    private String smallFileId;
}
