package faang.school.postservice.service.post.command;

import faang.school.postservice.api.media.MultipartFileMediaApi;
import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.mapper.post.MediaMapper;
import faang.school.postservice.mapper.post.MediaMapperImpl;
import faang.school.postservice.mapper.post.ResourceMapper;
import faang.school.postservice.mapper.post.ResourceMapperImpl;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.data.TestData;
import faang.school.postservice.service.resource.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdatePostResourceCommandTest {

    @Mock
    private ResourceService resourceService;

    private boolean mockWasInitialized = false;

    private UpdatePostResourceCommand updatePostResourceCommand;

    @BeforeEach
    void setUp() {
        if (mockWasInitialized) {
            return;
        }

        int poolSize = 5;
        ExecutorService executor = Executors.newFixedThreadPool(
                poolSize
        );

        int taskTimeout = 5;

        updatePostResourceCommand = new UpdatePostResourceCommand(
                resourceService,
                executor,
                taskTimeout
        );

        mockWasInitialized = true;
    }

    @Test
    @DisplayName("1. Test the creation of a new resource at the post")
    void testCreationNewResourceAtPost() {

        when(resourceService.createResources(1L,
                List.of(TestData.newAudioFile, TestData.newTextFile)

        )).thenReturn(
                List.of(
                        TestData.savedNewAudioFileResourceDto,
                        TestData.savedNewTextFileResourceDto
                ));

        List<UpdatableResourceDto> creatable = List.of(
                new UpdatableResourceDto(
                        null,
                        TestData.newAudioFile
                ),
                new UpdatableResourceDto(
                        null,
                        TestData.newTextFile
                )
        );

        List<ResourceDto> excepted = List.of(
                TestData.savedNewAudioFileResourceDto,
                TestData.savedNewTextFileResourceDto
        );
        List<ResourceDto> actual = updatePostResourceCommand.execute(
                TestData.EXISTENT_POST_ID, creatable
        );

        assertEquals(
                excepted,
                actual
        );
    }

    @Test
    @DisplayName("2. Test the post resource update")
    void testPostResourceUpdate() {


        List<UpdatableResourceDto> updatable = List.of(
                new UpdatableResourceDto(
                        TestData.savedAudioFileResource.getId(),
                        TestData.newAudioFile
                ),
                new UpdatableResourceDto(
                        TestData.savedTextFileResource.getId(),
                        TestData.newTextFile
                )
        );

        when(resourceService.updateResources(Map.of(
                TestData.savedAudioFileResource.getId(), TestData.newAudioFile,
                TestData.savedTextFileResource.getId(), TestData.newTextFile
        ))).thenReturn(
                List.of(
                        TestData.savedAudioFileResourceDto,
                        TestData.savedTextFileResourceDto
                )
        );

        List<ResourceDto> excepted = List.of(
                TestData.savedAudioFileResourceDto,
                TestData.savedTextFileResourceDto
        );
        List<ResourceDto> actual = updatePostResourceCommand.execute(
                TestData.EXISTENT_POST_ID,
                updatable
        );

        assertEquals(
                excepted,
                actual
        );
    }

    @Test
    @DisplayName("3. Test delete post resources")
    void testDeletePostResources() {

        List<UpdatableResourceDto> updatable = List.of(
                new UpdatableResourceDto(
                        TestData.savedAudioFileResource.getId(),
                        null
                ),
                new UpdatableResourceDto(
                        TestData.savedTextFileResource.getId(),
                        null
                )
        );

        updatePostResourceCommand.execute(1L, updatable);

        verify(resourceService, times(1)).deleteResources(
                Set.of(
                        TestData.savedAudioFileResource.getId(),
                        TestData.savedTextFileResource.getId()
                )
        );
    }

    @Test
    @DisplayName("4. Test complex updating resources of different types")
    void testUpdatingResourcesOfDifferentTypes() {

        List<UpdatableResourceDto> updatable = List.of(
                new UpdatableResourceDto(
                        null,
                        TestData.newVideFile
                ),
                new UpdatableResourceDto(
                        TestData.savedAudioFileResource.getId(),
                        TestData.newAudioFile
                ),
                new UpdatableResourceDto(
                        TestData.savedNewTextFileResource.getId(),
                        TestData.newTextFile
                ),
                new UpdatableResourceDto(
                        TestData.savedImageFileResource.getId(),
                        null
                )
        );

        when(resourceService.createResources(
                TestData.EXISTENT_POST_ID,
                List.of(TestData.newVideFile)
        )).thenReturn(
                List.of(TestData.savedNewVideoResourceDto)
        );

        when(resourceService.updateResources(
                Map.of(
                        TestData.savedAudioFileResource.getId(), TestData.newAudioFile,
                        TestData.savedNewTextFileResource.getId(), TestData.newTextFile
                )
        )).thenReturn(
                List.of(
                        TestData.savedNewAudioFileResourceDto,
                        TestData.savedNewTextFileResourceDto
                )
        );

        doNothing().when(resourceService).deleteResources(
                Set.of(TestData.savedImageFileResource.getId())
        );

        List<ResourceDto> excepted = Stream.of(
                TestData.savedNewVideoResourceDto,
                TestData.savedNewAudioFileResourceDto,
                TestData.savedNewTextFileResourceDto
        ).sorted(Comparator.comparing(ResourceDto::getId)).toList();

        List<ResourceDto> actual = updatePostResourceCommand.execute(TestData.EXISTENT_POST_ID, updatable).stream()
                .sorted(Comparator.comparing(ResourceDto::getId))
                .toList();

        verify(resourceService, times(1)).deleteResources(
                Set.of(TestData.savedImageFileResource.getId())
        );

        assertEquals(
                excepted,
                actual
        );

    }
}
