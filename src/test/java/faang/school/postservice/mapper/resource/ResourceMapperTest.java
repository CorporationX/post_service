package faang.school.postservice.mapper.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.model.resource.ResourceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResourceMapperTest {
    private final ResourceMapper mapper = new ResourceMapperImpl();

    @Test
    @DisplayName("mapping to dto")
    public void toResourceDtoTest() {
        Resource entity = getEntity(1L);
        ResourceDto dto = mapper.toResourceDto(entity);

        assertAll(
                () -> assertNotNull(dto),
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertEquals(mapper.getSize(entity.getSize()), dto.getSize()),
                () -> assertEquals(entity.getName(), dto.getName()),
                () -> assertEquals(entity.getType(), dto.getType()),
                () -> assertEquals(entity.getStatus(), dto.getStatus()),
                () -> assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("mapping to dto, null input")
    public void toResourceDtoNullTest() {
        ResourceDto dto = mapper.toResourceDto(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("mapping list to dto")
    public void toResourceDtoListTest() {
        Resource entity = getEntity(1L);
        Resource anotherEntity = getEntity(2L);
        List<Resource> entities = Arrays.asList(entity, anotherEntity);
        List<ResourceDto> dtos = mapper.toResourceDtoList(entities);

        assertAll(
                () -> assertNotNull(dtos),
                () -> assertEquals(entities.size(), dtos.size()),
                () -> assertEquals(entities.get(0).getId(), dtos.get(0).getId()),
                () -> assertEquals(mapper.getSize(entities.get(0).getSize()), dtos.get(0).getSize()),
                () -> assertEquals(entities.get(0).getName(), dtos.get(0).getName()),
                () -> assertEquals(entities.get(0).getType(), dtos.get(0).getType()),
                () -> assertEquals(entities.get(0).getStatus(), dtos.get(0).getStatus()),
                () -> assertEquals(entities.get(0).getUpdatedAt(), dtos.get(0).getUpdatedAt()),
                () -> assertEquals(entities.get(1).getId(), dtos.get(1).getId()),
                () -> assertEquals(mapper.getSize(entities.get(1).getSize()), dtos.get(1).getSize()),
                () -> assertEquals(entities.get(1).getName(), dtos.get(1).getName()),
                () -> assertEquals(entities.get(1).getType(), dtos.get(1).getType()),
                () -> assertEquals(entities.get(1).getStatus(), dtos.get(1).getStatus()),
                () -> assertEquals(entities.get(1).getUpdatedAt(), dtos.get(1).getUpdatedAt())
        );
    }

    @Test
    @DisplayName("mapping list to dto, null input")
    public void toResourceDtoListNullTest() {
        List<ResourceDto> dtos = mapper.toResourceDtoList(null);

        assertNull(dtos);
    }

    private Resource getEntity(Long id) {
        Resource entity = new Resource();
        entity.setId(id);
        entity.setSize(1024L);
        entity.setName("test-name");
        entity.setType("test-type");
        entity.setStatus(ResourceStatus.ACTIVE);
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
