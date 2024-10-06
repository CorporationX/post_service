package faang.school.postservice.service.resource.validator;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import faang.school.postservice.model.ResourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ImageFileValidatorTest {
    private final ImageFileValidator validator = new ImageFileValidator(5242880, 1);
    private final MultipartFile file = mock(MultipartFile.class);


    @Test
    @DisplayName("testValidateSize_Invalid")
    public void testValidateSize_Invalid() {
        when(file.getSize()).thenReturn(5242881L);

        assertThrows(FileException.class, () -> validator.validateSize(file));
    }

    @Test
    @DisplayName("testValidateAmount_Invalid")
    public void testValidateAmount_Invalid() {
        ResourceEntity resource = ResourceEntity.builder()
                .type(ResourceType.IMAGE)
                .build();

        Post post = Post.builder()
                .resourceEntities(List.of(resource))
                .build();

        assertThrows(FileException.class, () -> validator.validateAmount(ResourceType.IMAGE, post));
    }
}
