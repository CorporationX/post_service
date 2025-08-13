package faang.school.postservice.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedDto {
    private LinkedHashSet<Long> feed;
}
