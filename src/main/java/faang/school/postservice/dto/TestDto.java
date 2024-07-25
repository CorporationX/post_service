package faang.school.postservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TestDto {
    @Max(255)
    private String name;
    @Min(18)
    @Max(30)
    private int age;
}
