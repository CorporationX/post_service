package faang.school.postservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


/**
 * @brief Class is a representation of updatable media resources of post.
 * @details Objects of this class manage the process of updating media resources of post,
 * describing what operations should be performed on the resource.
 * Depending on the set fields, the object can be in one of three states: create, update, delete.
 * <p>
 * Description of states:
 * </p>
 * <p>
 * <b>Create state</b> - an object is in the <b>create</b> state if the {@code resource} field is set
 * and the {@code resourceId} field is null.
 * </p>
 * <p>
 * <b>Update state</b> - an object is in the <b>update</b> state if the {@code resource} field is set (not {@code null})
 * and the {@code resourceId} field is set.
 * </p>
 * <p>
 * <b>Delete state</b> - an object is in the <b>delete</b> state if the {@code resource} field is not set ({@code null})
 * and the {@code resourceId} field is set.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdatableResourceDto {
    private Long resourceId;
    private MultipartFile resource;

    public boolean isCreatableState(){
        return resourceId == null && resource != null;
    }

    public boolean isDeletableState(){
        return resourceId != null && resource == null;
    }

    public boolean isUpdatableState(){
        return resourceId != null && resource != null;
    }
}
