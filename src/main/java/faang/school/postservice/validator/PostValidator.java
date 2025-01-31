package faang.school.postservice.validator;

import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {
    private final UserExistValidator userExistValidator;
    private final ProjectExistValidator projectExistValidator;

    public void validatePost(PostSaveDto postSaveDto) {
        if (postSaveDto.getAuthorId() == null && postSaveDto.getProjectId() == null) {
            throw new DataValidationException("Должно быть заполнено одно из значений: authorId или projectId");
        } else {
            if (postSaveDto.getAuthorId() != null) {
                userExistValidator.userExist(postSaveDto.getAuthorId());
            } else {
                projectExistValidator.projectExist(postSaveDto.getProjectId());
            }
        }
        if (postSaveDto.getContent() == null || postSaveDto.getContent().isBlank()) {
            throw new DataValidationException("Контент не может быть пустым");
        } else if (postSaveDto.getContent().length() > 4096) {
            throw new DataValidationException("Длина контента не может превышать 4096 символов");
        }
    }

}
