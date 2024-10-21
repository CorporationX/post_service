package faang.school.postservice.service.post;

import faang.school.postservice.data.TestData;
import faang.school.postservice.dto.post.*;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.post.UnexistentPostException;
import faang.school.postservice.mapper.post.*;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.CacheService;
import faang.school.postservice.service.feed.FeedEventService;
import faang.school.postservice.service.post.command.UpdatePostResourceCommand;
import faang.school.postservice.service.publisher.PostEventPublisher;
import faang.school.postservice.validator.post.PostServiceValidator;
import faang.school.postservice.service.resource.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ResourceService resourceService;

    @Mock
    UpdatePostResourceCommand updatePostResourceCommand;

    @Spy
    private PostMapper postMapper = new PostMapperImpl();

    @Spy
    private ResourceMapper resourceMapper = new ResourceMapperImpl();

    @Mock
    private PostServiceValidator postServiceValidator;
    @Mock
    private PostEventPublisher postEventPublisher;
    @Mock
    private FeedEventService feedEventService;
    @Mock
    private CacheService cacheService;

    @InjectMocks
    private PostService postService;

    private record CreatePostDraftTestParam(
            DraftPostDto draft,
            Post creatablePost,
            Post savedPost,
            List<MultipartFile> creatableMedia,
            List<ResourceDto> savedResource,
            PostDto expectedSavedPost
    ) {
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestDataForCreateDraftMethod")
    @DisplayName("Test PostService.createPostDraft")
    void createPostDraftTest(String testCaseName, CreatePostDraftTestParam p) {
        initMockForCreatePostDraftTests(p);

        var draft = p.draft;

        var expected = p.expectedSavedPost;
        var actual = postService.createPostDraft(draft);

        assertEquals(expected, actual);
    }

    private void initMockForCreatePostDraftTests(CreatePostDraftTestParam p) {
        when(postRepository.save(
                p.creatablePost
        )).thenReturn(p.savedPost);

        if (p.creatableMedia != null && !p.creatableMedia.isEmpty()) {
            when(resourceService.createResources(
                    p.savedPost.getId(),
                    p.creatableMedia
            )).thenReturn(p.savedResource);
        }
    }

    private static Stream<Arguments> provideTestDataForCreateDraftMethod() {
        List<Arguments> testData = new ArrayList<>();

        // Test 1
        testData.add(
                Arguments.of(
                        "1. Test create draft without medias.",
                        new CreatePostDraftTestParam(
                                TestData.correctDraftWithoutMedia,
                                TestData.creatablePostWithoutMedia,
                                TestData.savedPostWithoutMedia,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                TestData.savedPostDtoWithoutMedia
                        )
                )
        );

        // Test 2

        testData.add(Arguments.of(
                "2. Test create draft with text media file.",
                new CreatePostDraftTestParam(
                        TestData.correctDraftWithTextFile,
                        TestData.creatablePostWithTextFile,
                        TestData.savedPostWithTextFile,
                        List.of(TestData.textFile),
                        List.of(TestData.savedTextFileResourceDto),
                        TestData.savedPostDtoWithTextFile
                )
        ));

        // Test 3

        testData.add(Arguments.of(
                "3. Test create draft with image media file.",
                new CreatePostDraftTestParam(
                        TestData.correctDraftWithImageFile,
                        TestData.creatablePostWithImageFile,
                        TestData.savedPostWithImageFile,
                        List.of(TestData.imageFile),
                        List.of(TestData.savedImageFileResourceDto),
                        TestData.savedPostDtoWithImageFile
                )
        ));

        // Test 4

        testData.add(Arguments.of(
                "4. Test create draft with video media file.",
                new CreatePostDraftTestParam(
                        TestData.correctDraftWithVideoFile,
                        TestData.creatablePostWithVideo,
                        TestData.savedPostWithVideo,
                        List.of(TestData.videoFile),
                        List.of(TestData.savedVideoFileResourceDto),
                        TestData.savedPostDtoWithVideo
                )
        ));

        // Test 5

        testData.add(Arguments.of(
                "5. Test create draft with audio media file",
                new CreatePostDraftTestParam(
                        TestData.correctDraftWithAudioFile,
                        TestData.creatablePostWithAudioFile,
                        TestData.savedPostWithAudioFile,
                        List.of(TestData.audioFile),
                        List.of(TestData.savedAudioFileResourceDto),
                        TestData.savedPostDtoWithAudioFile
                )
        ));

        // Test 6

        testData.add(Arguments.of(
                "6. Test create draft with multiple files",
                new CreatePostDraftTestParam(
                        TestData.correctDraftWithMultipleFiles,
                        TestData.creatablePostWithMultipleFile,
                        TestData.savedPostWithMultipleFile,
                        List.of(
                                TestData.textFile,
                                TestData.imageFile,
                                TestData.videoFile,
                                TestData.audioFile
                        ),
                        List.of(
                                TestData.savedTextFileResourceDto,
                                TestData.savedImageFileResourceDto,
                                TestData.savedVideoFileResourceDto,
                                TestData.savedAudioFileResourceDto
                        ),
                        TestData.savedPostDtoWithMultipleFiles
                )
        ));

        return testData.stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestDataForUpdatePostMethod")
    @DisplayName("Test PostService.updatePost")
    void testUpdatePost(String testCaseName, UpdatePostTestParam param) {
        initMockForUpdatePostTest(param);

        if (param.expectedException == null) {
            var updatable = param.updatablePostDto;

            var expected = param.expectedUpdatedPost;
            var actual = postService.updatePost(updatable);

            assertEquals(expected, actual);
        } else {
            assertThrows(
                    param.expectedException,
                    () -> postService.updatePost(param.updatablePostDto)
            );
        }
    }

    void initMockForUpdatePostTest(UpdatePostTestParam p) {

        if (Objects.equals(p.updatablePostDto.getPostId(), TestData.NON_EXISTENT_ID)) {
            when(postRepository.findById(
                    TestData.NON_EXISTENT_ID)
            ).thenReturn(Optional.empty());

            return;
        }

        when(postRepository.findById(
                        p.updatablePostDto.getPostId()
                )
        ).thenReturn(Optional.of(p.updatablePost));

        if (p.updatablePostDto.getResource() != null) {
            when(updatePostResourceCommand.execute(
                    p.updatablePostDto.getPostId(),
                    p.updatablePostDto.getResource()
            )).thenReturn(p.updatedResources);
        }

        var saved = p.savedUpdatedPost;

        when(postRepository.save(
                argThat(o ->
                        o.getId() == saved.getId() &&
                                Objects.equals(o.getContent(), saved.getContent()) &&
                                Objects.equals(o.getAuthorId(), saved.getAuthorId()) &&
                                Objects.equals(o.getProjectId(), saved.getProjectId()) &&
                                Objects.equals(o.getLikes(), saved.getLikes()) &&
                                Objects.equals(o.getComments(), saved.getComments()) &&
                                Objects.equals(o.getAlbums(), saved.getAlbums()) &&
                                Objects.equals(o.getAd(), saved.getAd()) &&
//                                Objects.equals(o.getResources(), saved.getResources()) &&
                                o.isPublished() == saved.isPublished() &&
                                Objects.equals(o.getPublishedAt(), saved.getPublishedAt()) &&
                                Objects.equals(o.getScheduledAt(), saved.getScheduledAt()) &&
                                o.isDeleted() == saved.isDeleted() &&
                                Objects.equals(o.getCreatedAt(), saved.getCreatedAt())
                )
        )).thenReturn(p.savedUpdatedPost);
    }

    private record UpdatePostTestParam(
            UpdatablePostDto updatablePostDto,
            Post updatablePost,
            Post savedUpdatedPost,
            List<ResourceDto> updatedResources,
            Class<? extends Throwable> expectedException,
            PostDto expectedUpdatedPost
    ) {
    }

    private static Stream<Arguments> provideTestDataForUpdatePostMethod() {
        List<Arguments> testData = new ArrayList<>();

        // Test 1

        testData.add(Arguments.of(
                "1. Test update content of post.",
                new UpdatePostTestParam(
                        TestData.updatePostContent,
                        TestData.storedPostWithTextFile.toBuilder().build(),
                        TestData.postWithUpdatedContent,
                        null,
                        null,
                        TestData.postDtoWithUpdatedContent
                )
        ));

        // Test 2

        testData.add(Arguments.of(
                "2. Test update scheduled time publication of post.",
                new UpdatePostTestParam(
                        TestData.updateScheduleAtPost,
                        TestData.storedPostWithTextFile.toBuilder().build(),
                        TestData.postWithUpdatedScheduledAt,
                        null,
                        null,
                        TestData.postDtoWithUpdatedScheduledAt
                )
        ));

        // Test 3

        testData.add(Arguments.of(
                "3. Test delete scheduled time publication of post.",
                new UpdatePostTestParam(
                        TestData.deleteScheduleAtPost,
                        TestData.storedPostWithTextFile.toBuilder().build(),
                        TestData.postWithDeletedScheduledAt,
                        null,
                        null,
                        TestData.postDtoWithDeletedScheduledAt
                )
        ));

        // Test 4

        testData.add(Arguments.of(
                "4. Test update post resources.",
                new UpdatePostTestParam(
                        TestData.updateResourcePost,
                        TestData.storedPostWithTextFile.toBuilder().build(),
                        TestData.postWithUpdatedResources,
                        List.of(TestData.savedNewTextFileResourceDto),
                        null,
                        TestData.postDtoWithUpdatedResources
                )
        ));

        // Test 5

        testData.add(Arguments.of(
                "5. Test update non existent post.",
                new UpdatePostTestParam(
                        TestData.updatableNonExistentPost,
                        null,
                        null,
                        null,
                        UnexistentPostException.class,
                        null
                )
        ));

        testData.add(Arguments.of(
                "6. Test complex update multiple fields",
                new UpdatePostTestParam(
                        TestData.updateMultipleFieldsPost,
                        TestData.storedPostWithMultipleUpdatableFields.toBuilder().build(),
                        TestData.updatedPostWithMultipleFields,
                        List.of(
                                TestData.savedNewVideoResourceDto,
                                TestData.savedNewAudioFileResourceDto
                        ),
                        null,
                        TestData.updatedPostDtoWithMultipleFields
                )
        ));

        return testData.stream();
    }

    private record GetPostsTestParam(
            GetPostsDto getPostsDto,
            List<Post> posts,
            List<PostDto> expectedPostsDto
    ) {
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestDataForGetPostsMethod")
    @DisplayName("Test PostService.getPosts")
    void testGetPosts(String testCaseName, GetPostsTestParam p) {

        initMockForGetPostsTest(p);

        var requestPosts = p.getPostsDto;

        var expected = p.expectedPostsDto;
        var actual = postService.getPosts(requestPosts);

        assertEquals(expected, actual);
    }

    void initMockForGetPostsTest(GetPostsTestParam p) {
        var request = p.getPostsDto;

        if (request.getAuthorId() != null) {
            when(
                    postRepository.findByAuthorId(
                            request.getAuthorId()
                    )
            ).thenReturn(
                    p.posts
            );
        } else if (request.getProjectId() != null) {
            when(
                    postRepository.findByProjectId(
                            request.getProjectId()
                    )
            ).thenReturn(
                    p.posts
            );
        } else {
            throw new RuntimeException("Failed initial mock for testGetPosts");
        }
    }

    private static Stream<Arguments> provideTestDataForGetPostsMethod() {
        List<Arguments> testData = new ArrayList<>();

        testData.add(Arguments.of(
                "1. Get all draft posts created by the project",
                new GetPostsTestParam(
                        new GetPostsDto(
                                null,
                                TestData.EXISTENT_PROJECT_ID,
                                PostStatus.DRAFT
                        ),
                        List.of(
                                TestData.createdProjectDraft1,
                                TestData.createdProjectDraft2
                        ),
                        List.of(
                                TestData.createdProjectDraftPostDto2,
                                TestData.createdProjectDraftPostDto1
                        )
                )
        ));

        testData.add(Arguments.of(
                "2. Get all published posts created by the project",
                new GetPostsTestParam(
                        new GetPostsDto(
                                null,
                                TestData.EXISTENT_PROJECT_ID,
                                PostStatus.POST
                        ),
                        List.of(
                                TestData.publishedProjectPost1,
                                TestData.publishedProjectPost2
                        ),
                        List.of(
                                TestData.publishedProjectPostDto2,
                                TestData.publishedProjectPostDto1
                        )
                )
        ));

        testData.add(Arguments.of(
                "3. Get all draft posts created by the author",
                new GetPostsTestParam(
                        new GetPostsDto(
                                TestData.EXISTENT_AUTHOR_ID,
                                null,
                                PostStatus.DRAFT
                        ),
                        List.of(
                                TestData.createdAuthorDraft1,
                                TestData.createdAuthorDraft2
                        ),
                        List.of(
                                TestData.createdAuthorDraftPostDto2,
                                TestData.createdAuthorDraftPostDto1
                        )
                )
        ));

        testData.add(Arguments.of(
                "4. Get all published posts created by the author",
                new GetPostsTestParam(
                        new GetPostsDto(
                                TestData.EXISTENT_AUTHOR_ID,
                                null,
                                PostStatus.POST
                        ),
                        List.of(
                                TestData.publishedAuthorPost1,
                                TestData.publishedAuthorPost2
                        ),
                        List.of(
                                TestData.publishedAuthorPostDto2,
                                TestData.publishedAuthorPostDto1
                        )
                )
        ));

        return testData.stream();
    }

    @Tag("postService.publishPost")
    @Test
    @DisplayName("Test publish post")
    void testPublishPost() {
        when(postRepository.findById(
                TestData.storedPostWithTextFile.getId()
        )).thenReturn(Optional.of(TestData.storedPostWithTextFile.toBuilder().build()));

        Post publishedPost = TestData.storedPostWithTextFile.toBuilder()
                .published(true)
                .publishedAt(any())
                .build();

        when(postRepository.save(
                publishedPost
        )).thenReturn(publishedPost);

        var expectedPublishedPost = TestData.savedPostDtoWithTextFile.toBuilder()
                .publishedAt(any())
                .build();

        var actualPublishedPost = postService.publishPost(TestData.storedPostWithTextFile.getId());

        assertEquals(expectedPublishedPost, actualPublishedPost);
    }

    @Tag("pstService.publishPost")
    @Test
    @DisplayName("Test attempt to publish non existent post")
    void testPublishPostNonExistentPost() {
        assertThrows(
                UnexistentPostException.class,
                () -> postService.publishPost(TestData.NON_EXISTENT_ID)
        );
    }

    @Tag("postService.deletePost")
    @Test
    @DisplayName("Test delete post")
    void testDeletePost() {
        when(postRepository.findById(
                TestData.storedPostWithTextFile.getId()
        )).thenReturn(Optional.of(
                TestData.storedPostWithTextFile.toBuilder().build()
        ));

        Post deletedPost = TestData.storedPostWithTextFile.toBuilder()
                .deleted(true)
                .build();

        postService.deletePost(TestData.storedPostWithTextFile.getId());

        verify(postRepository, times(1)).save(
                deletedPost
        );
    }

    @Tag("postService.deletePost")
    @Test
    @DisplayName("Test attempt delete non existent post")
    void testDeleteNonExistentPost() {
        assertThrows(
                UnexistentPostException.class,
                () -> postService.publishPost(TestData.NON_EXISTENT_ID)
        );
    }

    @Tag("postService.findPost")
    @Test
    @DisplayName("Test to get post")
    void testGetPost() {
        when(postRepository.findById(
                TestData.storedPostWithTextFile.getId()
        )).thenReturn(Optional.of(
                TestData.storedPostWithTextFile.toBuilder().build()
        ));

        var expectedPost = TestData.savedPostDtoWithTextFile;
        var actualPost = postService.findPost(TestData.storedPostWithTextFile.getId());

        assertEquals(expectedPost, actualPost);
    }

    @Tag("postService.findPost")
    @Test
    @DisplayName("Test to get non-existent post")
    void testGetNonExistentPost() {
        assertThrows(
                UnexistentPostException.class,
                () -> postService.publishPost(TestData.NON_EXISTENT_ID)
        );
    }
}
