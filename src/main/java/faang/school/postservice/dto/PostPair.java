package faang.school.postservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
public record PostPair(Long postId, LocalDateTime publishedAt) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostPair postPair = (PostPair) o;
        return Objects.equals(postId, postPair.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }

}
