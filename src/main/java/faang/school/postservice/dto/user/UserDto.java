package faang.school.postservice.dto.user;

import faang.school.postservice.dto.country.CountryDto;
import faang.school.postservice.dto.goal.GoalDto;
import faang.school.postservice.dto.skill.SkillDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private boolean active;
    private String city;
    private Integer experience;
    private List<Long> followers;
    private List<Long> followees;
    private List<Long> mentors;
    private List<Long> mentees;
    private CountryDto country;
    private List<GoalDto> goals;
    private List<SkillDto> skills;
}