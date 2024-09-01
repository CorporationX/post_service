package faang.school.postservice.dto.resource;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class PostResourceDto {
    private final Long id;
    private final String name;
    private final String type;
    private final long size;
    private final InputStream resource;
}
