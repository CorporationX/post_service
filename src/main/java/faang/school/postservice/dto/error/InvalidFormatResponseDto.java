package faang.school.postservice.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvalidFormatResponseDto {
    private String error;
    private String field;
    private String expectedFormat;
}
