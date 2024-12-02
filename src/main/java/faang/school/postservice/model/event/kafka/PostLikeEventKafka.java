package faang.school.postservice.model.event.kafka;

import faang.school.postservice.model.dto.LikeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeEventKafka {
    LikeDto like;
}
