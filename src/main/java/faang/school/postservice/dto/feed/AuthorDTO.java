package faang.school.postservice.dto.feed;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorDTO {
    private Long id;
    private String username;
    private String avatarUrl;
}