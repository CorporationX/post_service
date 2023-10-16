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

    private long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private Integer experience;
    private Locale locale;
    private String telegramChatId;
    private String preference;
    private String smallFileId;
    private List<Long> followerIds;
}
