package faang.school.postservice.model;

import java.util.List;

public interface Likeable {
    long getId();

    List<Like> getLikes();
}
