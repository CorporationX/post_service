package faang.school.postservice.dto.hashtag;

import lombok.Data;

@Data
public class HashtagRequestDto {
    private String tag;
    private int page;
    private int size;
}
