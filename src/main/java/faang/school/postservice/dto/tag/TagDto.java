package faang.school.postservice.dto.tag;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDto implements Serializable {
    @Nullable
    private Long id;
    @NotBlank
    private String name;
}
