package faang.school.postservice.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;
    private Locale locale;

    public enum PreferredContact {
        EMAIL, PHONE, TELEGRAM
    }

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.username = name;
        this.email = email;
    }
}