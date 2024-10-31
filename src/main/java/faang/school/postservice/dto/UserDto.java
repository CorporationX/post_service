package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private boolean active;
    private String aboutMe;
    private long countryId;
    private String city;
    private Integer experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> followersIds;
    private List<Long> followeesIds;
    private List<Long> ownedEventsIds;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
    private List<Long> setGoalsIds;
    private List<Long> goalsIds;
}
