package faang.school.postservice.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVisibilityDto {
    private Long id;
    private Long userId;
    private Long albumId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}