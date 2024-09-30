package faang.school.postservice.dto.correcter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpellCheckDto {
    private boolean status;
    private CorrecterResponseDto response;
}
