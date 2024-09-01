package faang.school.postservice.service.resource;

import faang.school.postservice.api.media.MultipartFileMediaApi;
import faang.school.postservice.data.TestData;
import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.resource.PostResourceDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.post.MediaMapper;
import faang.school.postservice.mapper.post.MediaMapperImpl;
import faang.school.postservice.mapper.post.ResourceMapper;
import faang.school.postservice.mapper.post.ResourceMapperImpl;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.util.PostResourceDtoComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MultipartFileMediaApi mediaApi;

    @Spy
    private ResourceMapper resourceMapper = new ResourceMapperImpl();
    @Spy
    private MediaMapper mediaMapper = new MediaMapperImpl();

    private ResourceService resourceService;

    private boolean mockWasInitialized = false;

    @BeforeEach
    void setUp() {
        if (mockWasInitialized) {
            return;
        }

        resourceService = new ResourceService(
                resourceRepository,
                mediaApi,
                resourceMapper,
                mediaMapper
        );

        mockWasInitialized = true;
    }

    @Test
    @DisplayName("1. Test the creation of a new resource at the post")
    void testCreationNewResourceAtPost() {

        when(mediaApi.save(List.of(
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

        List<ResourceDto> excepted = List.of(
                TestData.savedNewAudioFileResourceDto,
                TestData.savedNewTextFileResourceDto
        );

        List<ResourceDto> actual = resourceService.createResources(
                TestData.EXISTENT_POST_ID,
                List.of(
                        TestData.newAudioFile, TestData.newTextFile
                )
        );

        assertEquals(
                excepted,
                actual
        );
    }

    @Test
    @DisplayName("2. Test the post resource update")
    void testPostResourceUpdate() {
        when(resourceRepository.findAllById(Set.of(
                TestData.savedAudioFileResource.getId(),
                TestData.savedTextFileResource.getId()
        ))).thenReturn(
                List.of(
                        TestData.savedAudioFileResource,
                        TestData.savedTextFileResource
                )
        );

        when(resourceRepository.findAllById(new HashSet<>(List.of(
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

        when(mediaApi.update(
                Map.of(
                        TestData.savedAudioFileResource.getKey(), TestData.newAudioFile,
                        TestData.savedTextFileResource.getKey(), TestData.newTextFile
                )
        )).thenReturn((List.of(
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

        ResourceDto updatedAudioResourceDto = TestData.savedAudioFileResourceDto.toBuilder()
                .size(updatedAudioMediaDto.getSize())
                .name(updatedAudioMediaDto.getName())
                .type(updatedAudioMediaDto.getType())
                .build();

        ResourceDto updatedTextResourceDto = TestData.savedTextFileResourceDto.toBuilder()
                .size(updatedTextMediaDto.getSize())
                .name(updatedTextMediaDto.getName())
                .type(updatedTextMediaDto.getType())
                .build();

        List<ResourceDto> excepted = List.of(
                updatedAudioResourceDto,
                updatedTextResourceDto
        );

        List<ResourceDto> actual = resourceService.updateResources(
                Map.of(
                        TestData.savedAudioFileResource.getId(), TestData.newAudioFile,
                        TestData.savedTextFileResource.getId(), TestData.newTextFile
                )
        );

        assertEquals(
                excepted,
                actual
        );
    }

    @Test
    @DisplayName("3. Test delete post resources")
    void testDeletePostResources() {
        when(resourceRepository.popAllByIds(Set.of(
                TestData.savedAudioFileResource.getId(),
                TestData.savedTextFileResource.getId()
        ))).thenReturn(
                List.of(
                        TestData.savedAudioFileResource,
                        TestData.savedTextFileResource
                )
        );

        var deletableKeys = Set.of(
                TestData.savedAudioFileResource.getKey(),
                TestData.savedTextFileResource.getKey()
        );

        doNothing().when(mediaApi).delete(deletableKeys);

        var deletableIds = Set.of(
                TestData.savedAudioFileResource.getId(),
                TestData.savedTextFileResource.getId()
        );

        resourceService.deleteResources(deletableIds);

        verify(mediaApi, times(1)).delete(
                deletableKeys
        );
    }

    @Test
    @DisplayName("4. Getting post resources")
    void testGetPostResources() throws IOException {

        when(resourceRepository.findAllByPostId(TestData.EXISTENT_POST_ID))
                .thenReturn(
                        List.of(
                                TestData.savedTextFileResource,
                                TestData.savedAudioFileResource
                        )
                );

        when(mediaApi.getInputStreams(
                Set.of(
                        TestData.savedTextFileResource.getKey(),
                        TestData.savedAudioFileResource.getKey()
                )
        )).thenReturn(
                Map.of(
                        TestData.savedTextFileResource.getKey(), TestData.textFile.getInputStream(),
                        TestData.savedAudioFileResource.getKey(), TestData.audioFile.getInputStream()
                )
        );

        List<PostResourceDto> expected = List.of(
                PostResourceDto.builder()
                        .id(TestData.savedTextFileResource.getId())
                        .name(TestData.savedTextFileResource.getName())
                        .type(TestData.savedTextFileResource.getType())
                        .size(TestData.savedTextFileResource.getSize())
                        .resource(TestData.textFile.getInputStream())
                        .build()
                ,
                PostResourceDto.builder()
                        .id(TestData.savedAudioFileResource.getId())
                        .name(TestData.savedAudioFileResource.getName())
                        .type(TestData.savedAudioFileResource.getType())
                        .size(TestData.savedAudioFileResource.getSize())
                        .resource(TestData.audioFile.getInputStream())
                        .build()
        );

        List<PostResourceDto> actual = resourceService.getPostResources(TestData.EXISTENT_POST_ID);

        assertThat(actual)
                .usingElementComparator(new PostResourceDtoComparator())
                .containsExactlyElementsOf(expected);
    }
}
