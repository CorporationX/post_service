package faang.school.postservice.test_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TestDto {
    @NotNull
    @NotBlank
    private String content;
}
