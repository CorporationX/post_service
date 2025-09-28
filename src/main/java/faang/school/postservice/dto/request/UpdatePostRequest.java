package faang.school.postservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters")
    private String content;
}
