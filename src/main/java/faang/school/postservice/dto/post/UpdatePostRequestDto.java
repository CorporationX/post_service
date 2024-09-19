package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequestDto {
    @NotNull(message = "Id can't be empty")
    private Long id;
    @NotBlank(message = "Message can't be blank or empty")
    @Length(max = 4096, message = "Maximum number of characters 4096 chairs")
    private String content;
}
