package faang.school.postservice.dto.resource;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class PostResourceDto {
    final Long id;
    final String name;
    final String type;
    final long size;
    final InputStream resource;
}
