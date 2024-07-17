package faang.school.postservice.dto.post;

import faang.school.postservice.annotation.ValidHashtag;
import lombok.Data;

@Data
public class HashtagDto {
    Long id;

    @ValidHashtag
    String name;
}
