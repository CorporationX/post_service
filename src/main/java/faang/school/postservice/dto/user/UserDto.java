package faang.school.postservice.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import faang.school.postservice.model.PreferredContact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Data
@Builder
@RedisHash("User")
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 16)
    private String password;

    @Email
    private String email;

    @NotNull
    private String phone;

    private boolean active;

    @NotNull
    private Long countryId;

    private PreferredContact preference;

    @JsonIgnore
    @TimeToLive(unit = TimeUnit.DAYS)
    private int ttl;
}
