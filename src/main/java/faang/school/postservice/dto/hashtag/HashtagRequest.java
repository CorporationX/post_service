package faang.school.postservice.dto.hashtag;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HashtagRequest {
    @NotEmpty
    private List<String> hashtagNames;
}