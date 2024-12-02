package faang.school.postservice.model.event.kafka;

import faang.school.postservice.model.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostViewEventKafka {
    private PostDto postDto;
}
