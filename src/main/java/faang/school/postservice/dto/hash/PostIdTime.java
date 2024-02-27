package faang.school.postservice.dto.hash;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

// кратко: кафка дубликаты не может отпрвлят а я могу

// длинно: данной реализации если придут два объекта с равными id и разными publishedAt, то они
// сохранятся в сете (не будут считаться за дубликаты)
// цель этого такое поведение -> если кафка отправит дубликаты то у них буду одинаковые id и publishedAt
// cледовательно они не пройдут, но если вдруг я сам захочу отправить дубликат то он пройдет
// так как объекты будут с одиаковыми айди но разным временем

@Data
public class PostIdTime implements Comparable<PostIdTime> {
    private final long id;
    private final LocalDateTime publishedAt;

    public PostIdTime(long id, LocalDateTime publishedAt) {
        this.id = id;
        this.publishedAt = publishedAt;
    }

    @Override
    public int compareTo(PostIdTime other) {
        int comparePublishedAt = other.publishedAt.compareTo(this.publishedAt);
        if (comparePublishedAt == 0) {
            return Long.compare(this.id, other.id);
        }
        return comparePublishedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostIdTime post = (PostIdTime) o;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}