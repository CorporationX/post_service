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
    String key;
    String name;
    long size;
    String type;
}
