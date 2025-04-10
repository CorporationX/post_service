package faang.school.postservice.dto.resourse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResourceDto {

    @Min(value = 1, message = "ID must be greater than or equal to 1.")
    private Long id;

    @NotNull(message = "Post ID cannot be null.")
    private long postId;

    @NotNull(message = "Key must not be null")
    private String key;

    @NotBlank(message = "Name cannot be blank.")
    private String name;

    private long size;
    private String type;
    private LocalDateTime createdAt;
}