package faang.school.postservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "User entity with it's id full username and email")
public class UserDto {
    @Schema(description = "User's identification number", example = "1",
    accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "User's full name", example = "Vasiliy Petrov")
    private String username;
    @Schema(description = "User's email address", example = "vasya_super@mail.ru")
    private String email;
    private String phone;
    private String aboutMe;
    private Long countryId;
    private String city;
    private String userProfilePicId;
}