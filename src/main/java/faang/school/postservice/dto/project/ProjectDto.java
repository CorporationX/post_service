package faang.school.postservice.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectDto(
    long id,
    String title
) {
}
