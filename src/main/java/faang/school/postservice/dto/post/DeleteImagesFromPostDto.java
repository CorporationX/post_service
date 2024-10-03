package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteImagesFromPostDto {
    @NotNull(message = "Resource ids cannot be null")
    @NotEmpty(message = "Resource ids cannot be empty")
    private List<Long> resourceIds;
}
