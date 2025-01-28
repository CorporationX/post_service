package faang.school.postservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Data
@Component
public class CacheAuthor {
    @Id
    private long id;

    private boolean user;
}
