package faang.school.postservice.dto.hash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPretty {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Long countryId;
}
