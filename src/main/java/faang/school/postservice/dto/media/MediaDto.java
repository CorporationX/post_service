package faang.school.postservice.dto.media;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class MediaDto {
    @NotNull
    private String key;
    private String name;
    private long size;
    private String type;
}
