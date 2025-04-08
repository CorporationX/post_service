package faang.school.postservice.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagAddedToPostDto implements Serializable {
    private Long id;
    private String name;
}