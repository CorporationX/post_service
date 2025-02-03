package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Long profilePicFileId;
    private Long profilePicSmallFileId;
    private List<Long> userFollowerIds;

    public UserDto(Long id, String username, String email) {
        this(id, username, email, null, null, null);
    }
}