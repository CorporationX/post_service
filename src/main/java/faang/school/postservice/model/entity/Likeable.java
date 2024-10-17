package faang.school.postservice.model.entity;

import faang.school.postservice.model.entity.Like;

import java.util.List;

public interface Likeable {
    long getId();

    List<Like> getLikes();
}
