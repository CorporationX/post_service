package faang.school.postservice.redis.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("authors")
public class AuthorCache implements Serializable {

    @Id
    private long id;
    private String username;
    private String smallFileId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorCache that = (AuthorCache) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
