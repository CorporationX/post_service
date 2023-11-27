package faang.school.postservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private String smallFileId;
    private List<Long> followerIds;
    private List<Long> followeeIds;
    private Integer experience;
    private Locale locale;
    private String telegramChatId;
    private PreferredContact preference;
}
