package faang.school.postservice.redis.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthorRedisDto {
    private Long id;
    private String username;
    private String email;
}
