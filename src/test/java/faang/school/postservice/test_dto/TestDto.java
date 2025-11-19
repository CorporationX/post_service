package faang.school.postservice.test_dto;

import jakarta.validation.constraints.NotBlank;

public class TestDto {
    @NotBlank
    private String content;
}
