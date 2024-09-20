package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.post.PostAlreadyPublished;
import faang.school.postservice.exception.post.PostDeletedException;
import faang.school.postservice.exception.post.UnexistentPostPublisher;
import faang.school.postservice.exception.validation.DataValidationException;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.data.TestData;
import faang.school.postservice.validator.post.PostServiceValidator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private ResourceRepository resourceRepository;

    private PostServiceValidator postServiceValidator;

    @BeforeEach
    void setUp() {

        postServiceValidator = new PostServiceValidator(
                userServiceClient,
                projectServiceClient,
                resourceRepository,
                TestData.OBSOLESCENCE_PERIOD_DATE_PUBLICATION,
                TestData.MAX_POST_RESOURCE
        );

        lenient().when(
                userServiceClient.getUser(TestData.EXISTENT_AUTHOR_ID)
        ).thenReturn(UserDto.builder().id(TestData.EXISTENT_AUTHOR_ID).build());

        lenient().when(
                userServiceClient.getUser(TestData.NON_EXISTENT_ID)
        ).thenReturn(null);

        lenient().when(
                projectServiceClient.getProject(TestData.EXISTENT_PROJECT_ID)
        ).thenReturn(ProjectDto.builder().id(TestData.EXISTENT_PROJECT_ID).build());

        lenient().when(
                projectServiceClient.getProject(TestData.NON_EXISTENT_ID)
        ).thenReturn(null);
    }

    @Tag("PostServiceValidator.validateCreatablePostDraft")
    @Test
    @DisplayName("Test validate draft without publishers")
    public void testValidCreatableDraft() {
        var draft = TestData.draftWithoutPublisher;

        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validateCreatablePostDraft(
                        draft
                )
        );
    }

    @Tag("PostServiceValidator.validateUpdatablePost")
    @Test
    @DisplayName("Test validate updatable post with blank content")
    void testValidUpdatablePostWithBlankContent() {
        var draft = TestData.updatablePostWithBlankContent;

        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validateUpdatablePost(draft)
        );
    }

    @Tag("PostServiceValidator.validateUpdatablePost")
    @Test
    @DisplayName("Test validate updatable post with empty content")
    void testValidUpdatablePostWithEmptyContent() {
        var draft = TestData.updatablePostWithEmptyContent;

        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validateUpdatablePost(draft)
        );
    }

    @Tag("PostServiceValidator.validateUpdatablePost")
    @Test
    @DisplayName("Test validate updatable post with invalid state of scheduledAt")
    void testValidUpdatablePostWithInvalidStateOfScheduledAt() {
        var updatable = TestData.updatablePostWithInvalidStateOfScheduleAt;

        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validateUpdatablePost(updatable)
        );
    }

    @Tag("PostServiceValidator.validateUpdatablePost")
    @Test
    @DisplayName("Test validate updatable post with outdated scheduledAt")
    void testValidUpdatablePostWithOutdatedScheduledAt() {
        var updatable = TestData.updatablePostWithOutdatedScheduleAt;

        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validateUpdatablePost(updatable)
        );
    }

    @Tag("PostServiceValidator.validateUpdatablePost")
    @Test
    @DisplayName("Test validate updatable post with invalid resource")
    void testValidUpdatablePostWithInvalidResource() {
        var updatable = TestData.updatablePostWithInvalidUpdatableRes;

        when(resourceRepository.findAllIdsByPostId(updatable.getPostId()))
                .thenReturn(Collections.emptySet());

        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validateUpdatablePost(updatable)
        );
    }

    @Tag("PostServiceValidator.validateUpdatablePost")
    @Test
    @DisplayName("Test validate correct updatable post")
    void testValidUpdatablePost() {

        Set<Long> ids = new HashSet<>();
        for (long i = 0; i < TestData.MAX_POST_RESOURCE - 1; i++) {
            ids.add(i);
        }

        when(resourceRepository.findAllIdsByPostId(1L)).thenReturn(ids);

        var updatable = TestData.correctUpdatablePost;

        postServiceValidator.validateUpdatablePost(updatable);
    }

    @Tag("PostServiceValidator.validateUpdatablePost")
    @Test
    @DisplayName("Test validate updatable post with exceeded limit of media")
    void testValidUpdatablePostWithExceededLimitOfMedia() {

        Set<Long> ids = new HashSet<>();
        for (long i = 0; i < TestData.MAX_POST_RESOURCE + 1; i++) {
            ids.add(i);
        }

        when(resourceRepository.findAllIdsByPostId(1L)).thenReturn(ids);

        var updatable = TestData.correctUpdatablePost.toBuilder()
                .postId(1L)
                .resource(List.of(

                        new UpdatableResourceDto(
                                1L,
                                TestData.newAudioFile
                        ),
                        new UpdatableResourceDto(
                                2L,
                                null
                        ),
                        new UpdatableResourceDto(
                                null,
                                TestData.newTextFile
                        )

                ))
                .build();

        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validateUpdatablePost(updatable)
        );
    }

    @Tag("PostServiceValidator.validateCreatablePostDraft")
    @Test
    @DisplayName("Test validate draft with non existent author")
    public void testValidCreatableDraftNonExistentAuthor() {
        var draft = TestData.draftWithNonExistentAuthor;

        assertThrows(
                UnexistentPostPublisher.class,
                () -> postServiceValidator.validateCreatablePostDraft(
                        draft
                )
        );
    }

    @Tag("PostServiceValidator.validateCreatablePostDraft")
    @Test
    @DisplayName("Test validate draft with non existent project")
    public void testValidCreatableDraftNonExistentProject() {
        var draft = TestData.draftWithNonExistentProject;

        assertThrows(
                UnexistentPostPublisher.class,
                () -> postServiceValidator.validateCreatablePostDraft(
                        draft
                )
        );
    }

    @Tag("PostServiceValidator.validateCreatablePostDraft")
    @Test
    @DisplayName("Test validate draft with outdated scheduledAt")
    public void testValidCreatableDraftWithOutdatedScheduledAt() {
        var draft = TestData.draftWithInvalidScheduledAt;

        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validateCreatablePostDraft(
                        draft
                )
        );
    }

    @Tag("PostServiceValidator.validateCreatablePostDraft")
    @Test
    @DisplayName("Test validate correct draft")
    public void testValidCreatableDraftCorrect() {
        var draft = TestData.correctDraftWithAudioFile;
        draft.setProjectId(null);

        postServiceValidator.validateCreatablePostDraft(draft);
    }

    @Tag("PostServiceValidator.validatePublishablePost")
    @Test
    @DisplayName("Test attempt publish deleted post")
    public void testValidPublishDeletedPost() {
        var post = TestData.storedPostWithAudioFile.toBuilder()
                .deleted(true)
                .build();

        assertThrows(
                PostDeletedException.class,
                () -> postServiceValidator.validatePublishablePost(post)
        );
    }

    @Tag("PostServiceValidator.validatePublishablePost")
    @Test
    @DisplayName("Test attempt publish already published post")
    public void testValidPublishedPost() {
        var post = TestData.alreadyPublishedPost;

        assertThrows(
                PostAlreadyPublished.class,
                () -> postServiceValidator.validatePublishablePost(post)
        );
    }

    @Tag("PostServiceValidator.validatePublishablePost")
    @Test
    @DisplayName("Test validate correct publishable post")
    public void testValidPublishablePost() {
        var post = TestData.storedPostWithImageFile;

        postServiceValidator.validatePublishablePost(post);
    }


    @Tag("PostServiceValidator.validateDeletablePost")
    @Test
    @DisplayName("Test attempt delete already deleted post")
    public void testValidDeletablePost() {
        var post = TestData.storedPostWithAudioFile.toBuilder()
                .deleted(true)
                .build();

        assertThrows(
                PostDeletedException.class,
                () -> postServiceValidator.validateDeletablePost(post)
        );
    }

    @Tag("PostServiceValidator.validateDeletablePost")
    @Test
    @DisplayName("Test validate correct deletable post")
    public void testValidDeletePost() {
        var post = TestData.storedPostWithAudioFile.toBuilder()
                .deleted(false)
                .build();

        postServiceValidator.validateDeletablePost(post);
    }

    @Tag("PostServiceValidator.verifyPostDeletion")
    @Test
    @DisplayName("Test verify already deleted post")
    public void testVerifyPostDeletion() {
        var post = TestData.storedPostWithAudioFile.toBuilder()
                .deleted(true)
                .build();

        assertThrows(
                PostDeletedException.class,
                () -> postServiceValidator.validateDeletablePost(post)
        );
    }

    @Tag("PostServiceValidator.verifyPostDeletion")
    @Test
    @DisplayName("Test verify not deleted post")
    public void testVerifyNotDeletedPost() {
        var post = TestData.storedPostWithAudioFile.toBuilder()
                .deleted(false)
                .build();


        postServiceValidator.validateDeletablePost(post);
    }

    @Tag("PostServiceValidator.validatePostPublisher")
    @Test
    @DisplayName("Test validate not specified publisher")
    public void testValidateNotSpecifiedPostPublisher() {
        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validatePostPublisher(null, null)
        );
    }

    @Tag("PostServiceValidator.validatePostPublisher")
    @Test
    @DisplayName("Test validate post with publisher collision")
    public void testValidatePostWithPublisherCollision() {
        assertThrows(
                DataValidationException.class,
                () -> postServiceValidator.validatePostPublisher(
                        TestData.EXISTENT_AUTHOR_ID, TestData.EXISTENT_PROJECT_ID
                )
        );
    }

    @Tag("PostServiceValidator.validatePostPublisher")
    @Test
    @DisplayName("Test validate non existent author publisher")
    public void testValidateNonExistentAuthorPublisher() {
        assertThrows(
                UnexistentPostPublisher.class,
                () -> postServiceValidator.validatePostPublisher(
                        TestData.NON_EXISTENT_ID, null
                )
        );
    }

    @Tag("PostServiceValidator.validatePostPublisher")
    @Test
    @DisplayName("Test validate non existent project publisher")
    public void testValidateNonExistentProjectPublisher() {
        assertThrows(
                UnexistentPostPublisher.class,
                () -> postServiceValidator.validatePostPublisher(
                        null, TestData.NON_EXISTENT_ID
                )
        );
    }

    @Tag("PostServiceValidator.validatePostPublisher")
    @Test
    @DisplayName("Test validate correct post publisher")
    public void testValidateCorrectPostPublisher() {
        postServiceValidator.validatePostPublisher(
                TestData.EXISTENT_AUTHOR_ID, null
        );
    }
}
