package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.mapper.ResourceMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Spy
    private ResourceMapperImpl resourceMapper;
    @Mock
    private PostValidator postValidator;
    @Mock
    private ResourceService resourceService;
    @InjectMocks
    private PostService postService;
    private final long existencePostId = 5L;
    private Post post;
    private Post updatePost;
    private PostDto postDto;
    private PostDto updatePostDto;
    List<MultipartFile> files = new ArrayList<>();
    List<Resource> resources = new ArrayList<>();
    List<Resource> updatedResources = new ArrayList<>();
    private final long notExistencePostId = existencePostId + 1;

    @BeforeEach
    void init() {
        MultipartFile mockFile = mock(MultipartFile.class);
        int filesAmount = 3;
        IntStream.range(0, filesAmount).forEach((i) ->
                files.add(mockFile)
        );

        int resourcesAmount = 3;
        IntStream.range(0, resourcesAmount).forEach((i) ->
                resources.add(Resource.builder().id((long) i).build())
        );

        post = Post.builder()
                .id(existencePostId)
                .content("content")
                .resources(resources)
                .build();

        postDto = PostDto.builder()
                .id(existencePostId)
                .content("content")
                .resourceIds(post.getResources().stream().map(Resource::getId).toList())
                .build();

        int updatedResourcesAmount = resourcesAmount - 1;
        IntStream.range(0, updatedResourcesAmount).forEach(i ->
                updatedResources.add(resources.get(i))
        );
        updatePost = Post.builder()
                .id(existencePostId)
                .content("content")
                .resources(updatedResources)
                .build();

        updatePostDto = PostDto.builder()
                .id(existencePostId)
                .content("content")
                .resourceIds(updatePost.getResources().stream().map(Resource::getId).toList())
                .build();

    }

    @Test
    void testGetPost_postExists_returnsPost() {
        mockFindById(true);

        Post postByService = postService.getPost(existencePostId);

        assertEquals(post, postByService);
    }

    @Test
    void testGetPost_postNotExists_throwsEntityNotFoundException() {
        mockFindById(false);

        assertThrows(
                EntityNotFoundException.class,
                () -> postService.getPost(notExistencePostId)
        );
    }

    @Test
    void testGetPostDto_postExists_returnsPostDto() {
        mockFindById(true);

        PostDto postDtoByService = postService.getPostDto(existencePostId);

        assertEquals(post.getId(), postDtoByService.getId());
        assertEquals(postDto, postDtoByService);
    }

    @Test
    void testGetPostDto_postNotExists_throwsEntityNotFoundException() {
        mockFindById(false);

        assertThrows(
                EntityNotFoundException.class,
                () -> postService.getPostDto(notExistencePostId)
        );
    }

    @Test
    void testCreatePost_withNoFiles_savePostWithNoFiles() {
        files = null;
        Post postToSave = postMapper.toEntity(postDto);
        PostDto expectedDto = postMapper.toDto(postToSave);

        when(postRepository.save(postToSave)).thenReturn(postToSave);

        PostDto postDtoByService = postService.createPost(postDto, files);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();

        assertEquals(postToSave, savedPost);
        assertEquals(expectedDto, postDtoByService);
    }

    @Test
    void testCreatePost_withFiles_savePostWithFiles() {
        List<ResourceDto> resourceDtos = resources.stream().map(resourceMapper::toDto).toList();
        List<Long> expectedResourceIds = new ArrayList<>(resourceDtos.stream().map(ResourceDto::getId).toList());
        expectedResourceIds.addAll(post.getResources().stream().map(Resource::getId).toList());
        Post postToSave = postMapper.toEntity(postDto);

        when(postRepository.save(postToSave)).thenReturn(post);
        when(resourceService.createResources(post, files)).thenReturn(resourceDtos);

        PostDto postDtoByService = postService.createPost(postDto, files);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();

        assertEquals(postToSave, savedPost);
        assertEquals(expectedResourceIds, postDtoByService.getResourceIds());
    }

    @Test
    void testUpdatePost_postNotExists_throwsEntityNotFoundException() {
        mockFindById(false);
        assertThrows(
                EntityNotFoundException.class,
                () -> postService.updatePost(notExistencePostId, postDto, files)
        );
    }

    @Test
    void testUpdatePost_postDtoWithNoOneResourceId_updatePostAndDeleteOneFile() {
        Post postToUpdate = mockFindById(true);
        postToUpdate.setResources(updatedResources);

        when(postRepository.save(any())).thenReturn(updatePost);

        PostDto postDtoByService = postService.updatePost(existencePostId, updatePostDto, null);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();


        assertEquals(updatePostDto.getContent(), postDtoByService.getContent());
        assertEquals(postMapper.toDto(updatePost), postMapper.toDto(savedPost));
        assertEquals(updatePostDto.getResourceIds().size(), savedPost.getResources().size());
    }

    private Post mockFindById(boolean exist) {
        if (!exist) {
            when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
            return null;
        }
        when(postRepository.findById(existencePostId)).thenReturn(Optional.of(post));
        return post;
    }
}
