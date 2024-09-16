package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @brief Class to represent the post being updated.
 * @details The class represents an updatable post,
 * the update will occur depending on the specified fields. Objects of this class manage the process of updating post.
 * <p>
 *     {@code postId} - post id, <u>mandatory</u> field, then lists the data available for updating in the post.
 * </p>
 * <p>
 *     {@code content} - text content of the post, if set (not null),
 *     must not be empty and must not consist of white separators.
 * </p>
 * <p>
 *     {@code resource} - updatable media resources of the post. If this filed set,
 *     depending on the state {@link UpdatablePostDto} of
 *     the element, then the resources will be either created, replaced, or deleted.
 * </p>
 * <p>
 *     {@code scheduledAt} - schedule time of publication of the post.
 *     If the field is set, then the {@code deleteScheduledAt} field <u>must be</u> {@code null},
 *     otherwise an exception will be thrown when trying to updating post.
 *     If the post being updated has already been published, the post {@code scheduledAt} will not be updated.
 * </p>
 * <p>
 *     {@code deleteScheduledAt} - flag that indicates that the schedule publication date should be removed.
 *     If the field is set, then field {@code scheduledAt} <u>must be</u> {@code null}
 * </p>
 * */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdatablePostDto {
    @NotNull
    private Long postId;
    private String content;
    private List<UpdatableResourceDto> resource;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledAt;
    private boolean deleteScheduledAt;
}