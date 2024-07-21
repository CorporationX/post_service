package faang.school.postservice.dto.hashtag;

import faang.school.postservice.annotation.ValidHashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HashtagDto {
    private Long id;

    @ValidHashtag
    private String name;
}
