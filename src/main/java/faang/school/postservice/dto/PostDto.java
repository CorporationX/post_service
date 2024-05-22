package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long id;

    @NotBlank(message = "Post content cannot be empty")
    @Size(min = 1, max = 4096, message = "Post content must contains less then 4096 symbols")
    private String content;

    //TODO: сделать одного автора и в валидаторе проверять что есть такой юзер или проект,
    // но при этом нужно не забыть про мапинг, у меня же в Post есть 2 поля authorId и projectId,
    // и нужно автора в нужное поле сохранять
    @NotNull(message = "The author must be an existing user or project in the system")
    private long authorId;
//    @NotNull(message = "The author must be an existing project in the system")
//    private Long projectId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<Long> likesId;
    private List<Long> commentsId;

    private boolean published;
    private boolean deleted;

}