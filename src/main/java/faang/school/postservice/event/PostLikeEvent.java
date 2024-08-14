package faang.school.postservice.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostLikeEvent extends Event {

    int postId;

    int postAuthorId;

    int actorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime createdAt;
}
