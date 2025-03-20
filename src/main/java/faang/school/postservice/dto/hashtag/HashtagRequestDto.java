package faang.school.postservice.dto.hashtag;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HashtagRequestDto {
    private String tag;
    private int page;
    private int size;
}
