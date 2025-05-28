package faang.school.postservice.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO для отображения информации о пользователе получаемой из userService.
 */
@Data
@Builder
public class UserViewDto {
    private Long id;
    private String phone;
    private String username;
    private Integer experience;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
}