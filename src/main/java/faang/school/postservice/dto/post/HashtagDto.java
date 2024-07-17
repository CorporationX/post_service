package faang.school.postservice.dto.post;

import faang.school.postservice.annotation.ValidHashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HashtagDto {
    Long id;

    @ValidHashtag
    String name;
}
