package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserNewsFeedDto {
    private Long id;
    private String username;
    private String email;
    private List<Long> followers;
}
