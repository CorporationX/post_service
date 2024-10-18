package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFeedDto {
    private Long id;
    private String username;
}
