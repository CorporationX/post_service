package faang.school.postservice.dto.post;

import faang.school.postservice.annotation.ValidHashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "hashtags")
public class HashtagDto {
    Long id;

    @ValidHashtag
    String name;
}
