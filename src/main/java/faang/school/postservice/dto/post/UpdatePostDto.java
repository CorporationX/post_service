package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatePostDto {
    private Long id;
    private String content;
}
