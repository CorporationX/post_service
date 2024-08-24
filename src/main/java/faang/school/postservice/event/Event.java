package faang.school.postservice.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public abstract class Event {

    int actorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime createdAt;
}
