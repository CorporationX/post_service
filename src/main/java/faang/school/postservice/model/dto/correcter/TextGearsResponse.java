package faang.school.postservice.model.dto.correcter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextGearsResponse<T> {
    private boolean status;
    private T response;
}
