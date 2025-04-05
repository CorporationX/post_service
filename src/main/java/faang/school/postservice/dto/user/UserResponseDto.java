package faang.school.postservice.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class UserResponseDto implements Serializable {
    @JsonProperty("id")
    private long id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")
    private String phone;

    private PreferredContact preference;

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }

    @JsonCreator
    public UserResponseDto() {
        // Необходимо для Jackson
    }
}
