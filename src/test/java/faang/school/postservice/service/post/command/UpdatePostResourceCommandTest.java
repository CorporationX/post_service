package faang.school.postservice.service.post.command;

import faang.school.postservice.api.MultipartFileMediaApi;
import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.mapper.post.MediaMapper;
import faang.school.postservice.mapper.post.MediaMapperImpl;
import faang.school.postservice.mapper.post.ResourceMapper;
import faang.school.postservice.mapper.post.ResourceMapperImpl;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdatePostResourceCommandTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MultipartFileMediaApi mediaApi;

    @Spy
    private ResourceMapper resourceMapper = new ResourceMapperImpl();
    @Spy
    private MediaMapper mediaMapper = new MediaMapperImpl();

    private UpdatePostResourceCommand updatePostResourceCommand;

    private boolean mockWasInitialized = false;

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
                resourceRepository,
                mediaApi,
                resourceMapper,
                mediaMapper,
                executor,
                taskTimeout
        );

        mockWasInitialized = true;
    }

    @Test
    @DisplayName("1. Test the creation of a new resource at the post")
    void testCreationNewResourceAtPost() {

        when(mediaApi.saveAll(List.of(
                        TestData.newAudioFile,
                        TestData.newTextFile
                )
        )).thenReturn(List.of(
                TestData.newAudioFileMediaDto,
                TestData.newTextFileMediaDto
        ));

        when(resourceRepository.saveAll(List.of(
                        TestData.creatableNewAudioFileResource,
                        TestData.creatableNewTextFileResource
                )
        )).thenReturn(List.of(
                        TestData.savedNewAudioFileResource,
                        TestData.savedNewTextFileResource
                )
        );

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

        List<Resource> excepted = List.of(
                TestData.savedNewAudioFileResource,
                TestData.savedNewTextFileResource
        );
        List<Resource> actual = updatePostResourceCommand.execute(
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
        lenient().when(resourceRepository.findAllById(new HashSet<>(List.of(
                TestData.savedAudioFileResource.getId(),
                TestData.savedTextFileResource.getId()
        )
        ))).thenReturn(
                List.of(
                        TestData.savedAudioFileResource,
                        TestData.savedTextFileResource
                )
        );

        lenient().when(resourceRepository.findAllById(new HashSet<>(List.of(
                        TestData.savedTextFileResource.getId(),
                        TestData.savedAudioFileResource.getId()
                ))
        )).thenReturn(
                List.of(
                        TestData.savedTextFileResource,
                        TestData.savedAudioFileResource
                )
        );

        MediaDto updatedAudioMediaDto = new MediaDto(
                TestData.savedAudioFileResource.getKey(),
                TestData.newAudioFile.getName(),
                TestData.newAudioFile.getSize(),
                TestData.newAudioFile.getContentType()
        );

        MediaDto updatedTextMediaDto = new MediaDto(
                TestData.savedTextFileResource.getKey(),
                TestData.newTextFile.getName(),
                TestData.newTextFile.getSize(),
                TestData.newTextFile.getContentType()
        );

        when(mediaApi.updateAll(
                List.of(
                        Pair.of(
                                TestData.savedAudioFileResource.getKey(),
                                TestData.newAudioFile
                        ),
                        Pair.of(
                                TestData.savedTextFileResource.getKey(),
                                TestData.newTextFile
                        )
                )
        )).thenReturn(Optional.of(List.of(
                        updatedAudioMediaDto,
                        updatedTextMediaDto
                ))
        );

        Resource updatedAudioResource = TestData.savedAudioFileResource.toBuilder()
                .size(updatedAudioMediaDto.getSize())
                .name(updatedAudioMediaDto.getName())
                .type(updatedAudioMediaDto.getType())
                .build();

        Resource updatedTextResource = TestData.savedTextFileResource.toBuilder()
                .size(updatedTextMediaDto.getSize())
                .name(updatedTextMediaDto.getName())
                .type(updatedTextMediaDto.getType())
                .build();

        when(resourceRepository.saveAll(List.of(
                updatedAudioResource,
                updatedTextResource
        ))).thenReturn(List.of(
                updatedAudioResource,
                updatedTextResource
        ));

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

        List<Resource> excepted = List.of(
                updatedAudioResource,
                updatedTextResource
        );
        List<Resource> actual = updatePostResourceCommand.execute(
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
        lenient().when(resourceRepository.popAllByIds(List.of(
                TestData.savedAudioFileResource.getId(),
                TestData.savedTextFileResource.getId()
        ))).thenReturn(
                List.of(
                        TestData.savedAudioFileResource,
                        TestData.savedTextFileResource
                )
        );

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

        verify(mediaApi, times(1)).deleteAll(
                List.of(
                        TestData.savedAudioFileResource.getKey(),
                        TestData.savedTextFileResource.getKey()
                )
        );
    }

    @Test
    @DisplayName("4. Test complex updating resources of different types")
    void testUpdatingResourcesOfDifferentTypes() {

        when(mediaApi.saveAll(
                List.of(TestData.newVideFile)
        )).thenReturn(
                List.of(TestData.newVideFileMediaDto)
        );

        when(resourceRepository.saveAll(
                List.of(TestData.creatableNewVideResource)
        )).thenReturn(
                List.of(TestData.savedNewVideoResource)
        );

        lenient().when(resourceRepository.findAllById(new HashSet<>(List.of(
                TestData.savedAudioFileResource.getId(),
                TestData.savedTextFileResource.getId()
        )
        ))).thenReturn(
                List.of(
                        TestData.savedAudioFileResource,
                        TestData.savedTextFileResource
                )
        );

        lenient().when(resourceRepository.findAllById(new HashSet<>(List.of(
                        TestData.savedTextFileResource.getId(),
                        TestData.savedAudioFileResource.getId()
                ))
        )).thenReturn(
                List.of(
                        TestData.savedTextFileResource,
                        TestData.savedAudioFileResource
                )
        );

        MediaDto updatedAudioMediaDto = new MediaDto(
                TestData.savedAudioFileResource.getKey(),
                TestData.newAudioFile.getName(),
                TestData.newAudioFile.getSize(),
                TestData.newAudioFile.getContentType()
        );

        MediaDto updatedTextMediaDto = new MediaDto(
                TestData.savedTextFileResource.getKey(),
                TestData.newTextFile.getName(),
                TestData.newTextFile.getSize(),
                TestData.newTextFile.getContentType()
        );

        when(mediaApi.updateAll(
                List.of(
                        Pair.of(
                                TestData.savedAudioFileResource.getKey(),
                                TestData.newAudioFile
                        ),
                        Pair.of(
                                TestData.savedTextFileResource.getKey(),
                                TestData.newTextFile
                        )
                )
        )).thenReturn(Optional.of(List.of(
                        updatedAudioMediaDto,
                        updatedTextMediaDto
                ))
        );

        Resource updatedAudioResource = TestData.savedAudioFileResource.toBuilder()
                .size(updatedAudioMediaDto.getSize())
                .name(updatedAudioMediaDto.getName())
                .type(updatedAudioMediaDto.getType())
                .build();

        Resource updatedTextResource = TestData.savedTextFileResource.toBuilder()
                .size(updatedTextMediaDto.getSize())
                .name(updatedTextMediaDto.getName())
                .type(updatedTextMediaDto.getType())
                .build();

        when(resourceRepository.saveAll(List.of(
                updatedAudioResource,
                updatedTextResource
        ))).thenReturn(List.of(
                updatedAudioResource,
                updatedTextResource
        ));

        lenient().when(resourceRepository.popAllByIds(List.of(
                TestData.savedImageFileResource.getId()
        ))).thenReturn(
                List.of(
                        TestData.savedImageFileResource
                )
        );


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

        List<Resource> excepted = Stream.of(
                TestData.savedNewVideoResource,
                TestData.savedNewAudioFileResource,
                TestData.savedNewTextFileResource
        ).sorted(Comparator.comparing(Resource::getId)).toList();

        List<Resource> actual = updatePostResourceCommand.execute(1L, updatable).stream()
                .sorted(Comparator.comparing(Resource::getId))
                .toList();

        assertEquals(
                excepted,
                actual
        );


        verify(mediaApi, times(1)).deleteAll(
                List.of(
                        TestData.savedImageFileResource.getKey()
                )
        );
    }
}
