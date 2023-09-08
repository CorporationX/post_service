package faang.school.postservice.dto.resource;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDtoInputStream {
    private ResourceDto resourceDto;
    private InputStream inputStream;
}
