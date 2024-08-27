package faang.school.postservice.dto.hashtag;

import faang.school.postservice.model.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HashtagResponse {
    List<Hashtag> hashtags;
}
