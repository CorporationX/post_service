package faang.school.postservice.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostLikeEvent extends Event {

    int postId;

    int postAuthorId;
}
