package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record TextCheckRequest(@NotBlank(message = "Post content could not be blank") String text) {
}
