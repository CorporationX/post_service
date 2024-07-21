package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.post.PostFilterRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.post.PostValidation;
import faang.school.postservice.validation.project.ProjectValidation;
import faang.school.postservice.validation.user.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostValidation postValidation;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private UserValidation userValidation;

    @Mock
    private ProjectValidation projectValidation;

    @Mock
    private PostFilterRepository authorFilterSpecification;


    private List<PostFilterRepository> postFilterRepository;

//    @InjectMocks
    private PostService postService;

    private PostCreateDto postCreateDto;
    PostUpdateDto postUpdatedDto;
    private Post post;
    private PostDto postDto;

    @BeforeEach
    public void init() {
        //postValidation = new PostValidation(userValidation, projectValidation);
        postUpdatedDto = PostUpdateDto.builder().id(1L).content("updated content").build();
        post = Post.builder().id(1L).content("content").authorId(null).projectId(1L).build();
        postFilterRepository = List.of(authorFilterSpecification);

        postService = new PostService(postRepository, postFilterRepository, postValidation, userValidation, projectValidation, postMapper);
    }

    @Test
    public void testCreatePostWithNullPostDto() {
        postCreateDto = null;

        assertThrows(DataValidationException.class, () -> postService.create(postCreateDto));
        verify(postValidation, never()).oneOfTheAuthorsIsNoNullable(any(), any());
        verify(postRepository, never()).save(any());
    }

    static Stream<Arguments> initIncorrectAuthorAndProjectIdPostCreate() {
        return Stream.of(
                Arguments.of(PostCreateDto.builder().content("content").projectId(null).authorId(null).build(), "Only one of projectId or authorId must be provided"),
                Arguments.of(PostCreateDto.builder().content("content").projectId(1L).authorId(1L).build(), "Only one of projectId or authorId must be provided")
        );
    }

    @ParameterizedTest
    @MethodSource("initIncorrectAuthorAndProjectIdPostCreate")
    public void testCreatePostWithIncorrectAuthorAndProjectId(PostCreateDto postCreateDto, String exceptedErrorMessage) {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.create(postCreateDto));

        assertEquals(exceptedErrorMessage, exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testCreatePostWIthDoesntExistUser() {
        postCreateDto = PostCreateDto.builder().content("content").authorId(1L).projectId(null).build();
        String errorMessage = String.format("User with %s doesn't exist", postCreateDto.getAuthorId());

        doThrow(new DataValidationException(errorMessage)).when(userValidation).doesUserExist(postCreateDto.getAuthorId());

        assertThrows(DataValidationException.class, () -> postService.create(postCreateDto));
        verify(projectValidation, never()).doesProjectExist(any());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testCreatePostWIthDoesntExistProject() {
        postCreateDto = PostCreateDto.builder().content("content").authorId(null).projectId(1L).build();
        String errorMessage = String.format("Project with %s doesn't exist", postCreateDto.getProjectId());

        doThrow(new DataValidationException(errorMessage)).when(projectValidation).doesProjectExist(postCreateDto.getProjectId());

        assertThrows(DataValidationException.class, () -> postService.create(postCreateDto));
        verify(userValidation, never()).doesUserExist(any());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testCreatePostSuccessfully() {
        postCreateDto = PostCreateDto.builder().content("content").authorId(null).projectId(1L).build();
        doNothing().when(projectValidation).doesProjectExist(postCreateDto.getProjectId());
        when(postMapper.toPost(postCreateDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);

        PostDto createdPost = postService.create(postCreateDto);

        assertEquals(post.getId(), createdPost.getId());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testPublishWithDoesntPost() {
        when(postRepository.findById(post.getId())).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> postService.publish(post.getId()));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testPublishWithAlreadyPublishedPost() {
        post.setPublished(true);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.publish(post.getId()));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testPublishSuccessfully() {
        Post postToUpdate = Post.builder().id(1L).content("content").authorId(null).projectId(1L).published(false).build();
        when(postRepository.findById(postToUpdate.getId())).thenReturn(Optional.of(postToUpdate));
        when(postRepository.updatePublishedStatus(any(), any(), any())).thenReturn(1);

        PostDto actualPublished = postService.publish(postToUpdate.getId());

        assertTrue(actualPublished.isPublished());
    }

    @Test
    public void testDeleteByIdWithDoesntExistPost() {
        Long id = 1L;
        when(postRepository.findById(id)).thenThrow(new DataValidationException(String.format("Post %s doesn't exist", id)));

        assertThrows(DataValidationException.class, () -> postService.delete(id));
    }

    @Test
    public void testDeleteByIdSuccessfully() {
        Post deletedPost = Post.builder().id(1L).content("content").authorId(null).projectId(1L).published(false).deleted(true).build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(deletedPost)).thenReturn(deletedPost);

        postService.delete(deletedPost.getId());

        verify(postRepository, times(1)).save(deletedPost);
        assertTrue(deletedPost.isDeleted());
    }

    @Test
    public void testGetByIdWithDoesntExistPost() {
        Long id = 1L;
        when(postRepository.findById(id)).thenThrow(new DataValidationException(String.format("Post %s doesn't exist", id)));

        assertThrows(DataValidationException.class, () -> postService.getById(id));
    }

    @Test
    public void testGetByIdSuccessfully() {
        Long id = 1L;
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostDto actual = postService.getById(id);

        assertEquals(post.getId(), actual.getId());
    }

    @Test
    public void testGetDraftOrPublishedPostsWithNullablePost() {
        assertThrows(DataValidationException.class, () -> postService.getPostsByPublishedStatus(null));
    }

    @Test
    public void testGetDraftOrPublishedPostsWithNullablePublishedField() {
        assertThrows(DataValidationException.class, () -> postService.getPostsByPublishedStatus(null));
    }

    static Stream<Arguments> initIncorrectAuthorAndProjectIdGetPosts() {
        return Stream.of(
                Arguments.of(PostFilterDto.builder().projectId(null).authorId(null).published(true).build(), "Only one of projectId or authorId must be provided"),
                Arguments.of(PostFilterDto.builder().projectId(1L).authorId(1L).published(true).build(), "Only one of projectId or authorId must be provided")
        );
    }

    @ParameterizedTest
    @MethodSource("initIncorrectAuthorAndProjectIdGetPosts")
    public void testGetDraftOrPublishedPostsWithIncorrectProjectOrUserFields(PostFilterDto filter, String exceptedErrorMessage) {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.getPostsByPublishedStatus(filter));

        assertEquals(exceptedErrorMessage, exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testGetDraftOrPublishedPostsWithNotExistUser() {
        PostFilterDto postFilter = PostFilterDto.builder().projectId(null).authorId(1L).published(true).build();
        doThrow(DataValidationException.class).when(userValidation).doesUserExist(postFilter.getAuthorId());

        assertThrows(DataValidationException.class, () -> postService.getPostsByPublishedStatus(postFilter));
    }

    @Test
    public void testGetDraftOrPublishedPostsWithNotExistProject() {
        PostFilterDto postFilter = PostFilterDto.builder().projectId(1L).authorId(null).published(true).build();
        doThrow(DataValidationException.class).when(projectValidation).doesProjectExist(postFilter.getProjectId());

        assertThrows(DataValidationException.class, () -> postService.getPostsByPublishedStatus(postFilter));
    }

    @Test
    public void testGetDraftOrPublishedPostsSuccessfully() {
        PostFilterDto postFilter = PostFilterDto.builder().projectId(1L).authorId(null).published(true).page(0).size(1).build();
        doNothing().when(projectValidation).doesProjectExist(postFilter.getProjectId());
        Page<Post> postPage = new PageImpl<>(Collections.singletonList(post));
        when(postFilterRepository.get(0).isApplicable(any())).thenReturn(true);
        when(postFilterRepository.get(0).apply(any())).thenReturn((root, query, builder) -> builder.equal(root.get("authorId"), postFilter.getAuthorId()));
        when(postRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(postPage);

        Page<PostDto> result = postService.getPostsByPublishedStatus(postFilter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(projectValidation, times(1)).doesProjectExist(anyLong());
        verify(postRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void updateWhenPostNullable() {
        postUpdatedDto = null;

        assertThrows(DataValidationException.class, () -> postService.update(postUpdatedDto));
    }

    static Stream<Arguments> initIncorrectPostUpdateFields() {
        return Stream.of(
                Arguments.of(PostUpdateDto.builder().id(null).build(), "Id can't be empty"),
                Arguments.of(PostUpdateDto.builder().id(1L).content(null).build(), "Content can't be empty"),
                Arguments.of(PostUpdateDto.builder().id(1L).content("ab").build(), "Size of post content must contains minimum 3 characters")
        );
    }

    @ParameterizedTest
    @MethodSource("initIncorrectPostUpdateFields")
    public void updateWhenPostUpdateFieldsInvalid(PostUpdateDto updateDto, String exceptedErrorMessage) {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.update(updateDto));

        assertEquals(exceptedErrorMessage, exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void updatePostWhenPostDoesNotExist() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.update(postUpdatedDto));

        assertEquals(String.format("Post %s doesn't exist", postUpdatedDto.getId()), exception.getMessage());
    }

    @Test
    void updatePostSuccessfully() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostDto result = postService.update(postUpdatedDto);

        assertNotNull(result);
    }
}
