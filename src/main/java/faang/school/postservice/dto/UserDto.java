package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
enum PreferredContact {
    EMAIL, PHONE, TELEGRAM;
}