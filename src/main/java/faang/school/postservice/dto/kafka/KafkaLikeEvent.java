package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaLikeEvent {

    private LikeDto like;
    private UserDto author;

}