package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record PostDto(
        Long id,

        String content,
        boolean published,

        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime publishedAt,
        boolean deleted,

        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime createdAt,

        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime updatedAt,

        @NotBlank(message = "post author cannot be empty")
        Long authorId,

        @NotBlank(message = "project for the post cannot be empty")
        Long projectId
) {
}
