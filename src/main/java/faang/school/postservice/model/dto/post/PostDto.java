package faang.school.postservice.model.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record PostDto(
        @Positive
        Long id,
        @Positive
        Long authorId,
        @Positive
        Long projectId,
        boolean deleted,

        @NotBlank(message = "Content can not be null or empty")
        @Size(max = 4096)
        String content,
        boolean published,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime publishedAt,

        @NotBlank(message = "Title can not be null or empty")
        @Size(max = 150)
        String title,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime updatedAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime scheduledAt
) {
}
