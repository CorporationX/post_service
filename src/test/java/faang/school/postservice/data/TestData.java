package faang.school.postservice.data;

import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.post.DraftPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatablePostDto;
import faang.school.postservice.dto.resource.PreviewPostResourceDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


public class TestData {

    public final static Long NON_EXISTENT_ID = -1L;
    public final static Long EXISTENT_AUTHOR_ID = 1L;
    public final static Long EXISTENT_PROJECT_ID = 2L;
    public final static Long EXISTENT_POST_ID = 1L;

    public static final long OBSOLESCENCE_PERIOD_DATE_PUBLICATION = 5L;
    public static final long MAX_POST_RESOURCE = 10L;

    public final static String correctContent = "content";
    public final static String newContent = "new content";
    public final static String emptyContent = "";
    public final static String blankContent = "        ";

    public final static String textContentType = "text/plain";
    public final static String imageContentType = "image/png";
    public final static String videoContentType = "video/mp4";
    public final static String audioContentType = "audio/mpeg";

    public final static String nameTextFile = "text_file.txt";
    public final static String nameNewTextFile = "new_text_file.txt";
    public final static String nameImageFile = "image_file.png";
    public final static String nameVideoFile = "video_file.mp4";
    public final static String nameAudioFile = "audio_file.mpeg";

    public final static LocalDateTime defaultCreateAt = LocalDateTime.parse("2025-07-28T03:39:36");
    public final static LocalDateTime defaultScheduledAt = LocalDateTime.parse("2025-07-28T03:45:36");
    public final static LocalDateTime defaultPublishedAt = LocalDateTime.parse("2025-07-28T03:45:36");
    public final static LocalDateTime newScheduleAt = defaultScheduledAt.plusMonths(1);

    public final static MockMultipartFile textFile = new MockMultipartFile(
            nameTextFile, nameTextFile, textContentType, correctContent.getBytes()
    );

    public final static MockMultipartFile newTextFile = new MockMultipartFile(
            nameNewTextFile, nameNewTextFile, textContentType, newContent.getBytes()
    );

    public final static MediaDto newTextFileMediaDto = MediaDto.builder()
            .key("1L")
            .size(newTextFile.getSize())
            .name(newTextFile.getName())
            .type(newTextFile.getContentType())
            .build();

    public final static Resource creatableNewTextFileResource = Resource.builder()
            .key("1L")
            .size(newTextFile.getSize())
            .name(newTextFile.getName())
            .type(newTextFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static Resource savedNewTextFileResource = creatableNewTextFileResource.toBuilder()
            .id(1L)
            .build();

    public final static ResourceDto savedNewTextFileResourceDto = ResourceDto.builder()
            .id(savedNewTextFileResource.getId())
            .key(savedNewTextFileResource.getKey())
            .size(savedNewTextFileResource.getSize())
            .name(savedNewTextFileResource.getName())
            .type(savedNewTextFileResource.getType())
            .build();

    public final static MediaDto textFileMediaDto = new MediaDto(
            "1L",
            textFile.getName(),
            textFile.getSize(),
            textFile.getContentType()
    );

    public final static Resource creatableTextFileResource = Resource.builder()
            .key("1L")
            .size(textFile.getSize())
            .name(textFile.getName())
            .type(textFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static Resource savedTextFileResource = Resource.builder()
            .id(1L)
            .key("1L")
            .size(textFile.getSize())
            .name(textFile.getName())
            .type(textFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static ResourceDto savedTextFileResourceDto = ResourceDto.builder()
            .id(savedTextFileResource.getId())
            .key(savedTextFileResource.getKey())
            .size(savedTextFileResource.getSize())
            .name(savedTextFileResource.getName())
            .type(savedTextFileResource.getType())
            .build();

    public final static MockMultipartFile imageFile = new MockMultipartFile(
            nameImageFile, nameImageFile, imageContentType, correctContent.getBytes()
    );

    public final static MediaDto imageFileMediaDto = new MediaDto(
            "2L",
            imageFile.getName(),
            imageFile.getSize(),
            imageFile.getContentType()
    );

    public final static Resource creatableImageFileResource = Resource.builder()
            .key("2L")
            .size(imageFile.getSize())
            .name(imageFile.getName())
            .type(imageFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static Resource savedImageFileResource = Resource.builder()
            .id(2L)
            .key("2L")
            .size(imageFile.getSize())
            .name(imageFile.getName())
            .type(imageFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static ResourceDto savedImageFileResourceDto = ResourceDto.builder()
            .id(savedImageFileResource.getId())
            .key(savedImageFileResource.getKey())
            .size(savedImageFileResource.getSize())
            .name(savedImageFileResource.getName())
            .type(savedImageFileResource.getType())
            .build();



    public final static MockMultipartFile videoFile = new MockMultipartFile(
            nameVideoFile, nameVideoFile, videoContentType, correctContent.getBytes()
    );

    public final static MediaDto videoFileMediaDto = new MediaDto(
            "3L",
            videoFile.getName(),
            videoFile.getSize(),
            videoFile.getContentType()
    );

    public final static Resource creatableVideoFileResource = Resource.builder()
            .key("3L")
            .size(videoFile.getSize())
            .name(videoFile.getName())
            .type(videoFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static Resource savedVideoFileResource = Resource.builder()
            .id(3L)
            .key("3L")
            .size(videoFile.getSize())
            .name(videoFile.getName())
            .type(videoFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static ResourceDto savedVideoFileResourceDto = ResourceDto.builder()
            .id(savedVideoFileResource.getId())
            .key(savedVideoFileResource.getKey())
            .size(savedVideoFileResource.getSize())
            .name(savedVideoFileResource.getName())
            .type(savedVideoFileResource.getType())
            .build();

    public final static MockMultipartFile newVideFile = new MockMultipartFile(
            "new_video.mp4",
            "new_video.mp4",
            videoContentType,
            correctContent.getBytes()
    );

    public final static MediaDto newVideFileMediaDto = new MediaDto(
            "7L",
            newVideFile.getName(),
            newVideFile.getSize(),
            newVideFile.getContentType()
    );

    public final static Resource creatableNewVideResource = Resource.builder()
            .key(newVideFileMediaDto.getKey())
            .name(newVideFileMediaDto.getName())
            .size(newVideFileMediaDto.getSize())
            .type(newVideFileMediaDto.getType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static Resource savedNewVideoResource = creatableNewVideResource.toBuilder()
            .id(7L)
            .build();

    public final static ResourceDto savedNewVideoResourceDto = ResourceDto.builder()
            .id(savedNewVideoResource.getId())
            .key(savedNewVideoResource.getKey())
            .name(savedNewVideoResource.getName())
            .size(savedNewVideoResource.getSize())
            .type(savedNewVideoResource.getType())
            .build();

    public final static MockMultipartFile newAudioFile = new MockMultipartFile(
            "new_audio.mpeg",
            "new_audio.mpeg",
            "audio/mpeg",
            "some new music".getBytes()
    );

    public final static MediaDto newAudioFileMediaDto = new MediaDto(
            "4L",
            newAudioFile.getName(),
            newAudioFile.getSize(),
            newAudioFile.getContentType()
    );

    public final static Resource creatableNewAudioFileResource = Resource.builder()
            .key("4L")
            .size(newAudioFile.getSize())
            .name(newAudioFile.getName())
            .type(newAudioFileMediaDto.getType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static Resource savedNewAudioFileResource = creatableNewAudioFileResource.
            toBuilder()
            .id(4L)
            .build();

    public final static ResourceDto savedNewAudioFileResourceDto = ResourceDto.builder()
            .id(savedNewAudioFileResource.getId())
            .key(savedNewAudioFileResource.getKey())
            .size(savedNewAudioFileResource.getSize())
            .name(savedNewAudioFileResource.getName())
            .type(savedNewAudioFileResource.getType())
            .build();

    public final static MockMultipartFile audioFile = new MockMultipartFile(
            nameAudioFile, nameAudioFile, audioContentType, correctContent.getBytes()
    );

    public final static MediaDto audioFileMediaDto = new MediaDto(
            "4L",
            audioFile.getName(),
            audioFile.getSize(),
            audioFile.getContentType()
    );

    public final static Resource creatableAudioFileResource = Resource.builder()
            .key("4L")
            .size(audioFile.getSize())
            .name(audioFile.getName())
            .type(audioFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static Resource savedAudioFileResource = Resource.builder()
            .id(4L)
            .key("4L")
            .size(audioFile.getSize())
            .name(audioFile.getName())
            .type(audioFile.getContentType())
            .post(Post.builder().id(EXISTENT_POST_ID).build())
            .build();

    public final static ResourceDto savedAudioFileResourceDto = ResourceDto.builder()
            .id(savedAudioFileResource.getId())
            .key(savedAudioFileResource.getKey())
            .size(savedAudioFileResource.getSize())
            .name(savedAudioFileResource.getName())
            .type(savedAudioFileResource.getType())
            .build();

    public final static LocalDateTime invalidScheduledAt = LocalDateTime.now()
            .minusMinutes(OBSOLESCENCE_PERIOD_DATE_PUBLICATION + 1);

    public final static DraftPostDto draftWithoutPublisher =
            DraftPostDto.builder()
                    .authorId(null)
                    .projectId(null)
                    .content(correctContent)
                    .build();

    public final static DraftPostDto draftWithNonExistentAuthor =
            DraftPostDto.builder()
                    .authorId(NON_EXISTENT_ID)
                    .content(correctContent)
                    .build();

    public final static DraftPostDto draftWithNonExistentProject =
            DraftPostDto.builder()
                    .projectId(NON_EXISTENT_ID)
                    .content(correctContent)
                    .build();


    public final static UpdatablePostDto updatablePostWithEmptyContent =
            UpdatablePostDto.builder()
                    .content(emptyContent)
                    .build();


    public final static UpdatablePostDto updatablePostWithBlankContent =
            UpdatablePostDto.builder()
                    .content(blankContent)
                    .build();

    public final static UpdatablePostDto updatablePostWithInvalidStateOfScheduleAt =
            UpdatablePostDto.builder()
                    .scheduledAt(defaultScheduledAt)
                    .deleteScheduledAt(true)
                    .build();

    public final static UpdatablePostDto updatablePostWithOutdatedScheduleAt =
            UpdatablePostDto.builder()
                    .scheduledAt(invalidScheduledAt)
                    .build();

    public final static UpdatablePostDto updatablePostWithInvalidUpdatableRes =
            UpdatablePostDto.builder()
                    .postId(EXISTENT_POST_ID)
                    .resource(List.of(
                            new UpdatableResourceDto(
                                    null, null
                            )
                    ))
                    .build();

    public final static UpdatablePostDto correctUpdatablePost =
            UpdatablePostDto.builder()
                    .postId(EXISTENT_POST_ID)
                    .content(correctContent)
                    .scheduledAt(LocalDateTime.now())
                    .resource(List.of(
                            new UpdatableResourceDto(
                                    null, newAudioFile
                            )
                    ))
                    .build();

    public final static DraftPostDto draftWithInvalidScheduledAt =
            DraftPostDto.builder()
                    .projectId(EXISTENT_PROJECT_ID)
                    .content(correctContent)
                    .scheduledAt(invalidScheduledAt)
                    .build();


    public final static DraftPostDto correctDraftWithoutMedia =
            DraftPostDto.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(defaultScheduledAt)
                    .build();

    public final static Post creatablePostWithoutMedia =
            Post.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(correctDraftWithoutMedia.getScheduledAt())
                    .build();

    public final static Post storedPostWithoutMedia =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(correctDraftWithoutMedia.getScheduledAt())
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .build();

    public final static Post savedPostWithoutMedia =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(correctDraftWithoutMedia.getScheduledAt())
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .build();

    public final static PostDto savedPostDtoWithoutMedia = PostDto.builder()
            .id(EXISTENT_POST_ID)
            .authorId(EXISTENT_AUTHOR_ID)
            .content(correctContent)
            .scheduledAt(correctDraftWithoutMedia.getScheduledAt())
            .createdAt(defaultCreateAt)
            .updatedAt(defaultCreateAt)
            .resources(Collections.emptyList())
            .commentsCount(0L)
            .likesCount(0L)
            .build();

    public final static DraftPostDto correctDraftWithTextFile =
            DraftPostDto.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(defaultScheduledAt)
                    .resource(List.of(textFile))
                    .build();

    public final static Post creatablePostWithTextFile =
            Post.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithTextFile.getScheduledAt()
                    )
                    .build();

    public final static Post storedPostWithTextFile =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithTextFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .resources(
                            List.of(
                                    savedTextFileResource
                            )
                    )
                    .build();

    public final static Post savedPostWithTextFile =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithTextFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .build();

    public final static PostDto savedPostDtoWithTextFile =
            PostDto.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(correctDraftWithTextFile.getScheduledAt())
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .resources(
                            List.of(
                                    new PreviewPostResourceDto(
                                            savedTextFileResource.getId(),
                                            savedTextFileResource.getName()
                                    )
                            )
                    )
                    .commentsCount(0L)
                    .likesCount(0L)
                    .build();

    public final static DraftPostDto correctDraftWithVideoFile =
            DraftPostDto.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(defaultScheduledAt)
                    .resource(List.of(videoFile))
                    .build();

    public final static Post creatablePostWithVideo =
            Post.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithVideoFile.getScheduledAt()
                    )
                    .build();


    public final static Post storedPostWithVideo =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithVideoFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .resources(
                            List.of(
                                    savedVideoFileResource
                            )
                    )
                    .build();

    public final static Post savedPostWithVideo =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithVideoFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .build();

    public final static PostDto savedPostDtoWithVideo =
            PostDto.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithVideoFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .resources(
                            List.of(
                                    new PreviewPostResourceDto(
                                            savedVideoFileResource.getId(),
                                            savedVideoFileResource.getName()
                                    )
                            )
                    )
                    .build();

    public final static DraftPostDto correctDraftWithImageFile =
            DraftPostDto.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(defaultScheduledAt)
                    .resource(List.of(imageFile))
                    .build();

    public final static Post creatablePostWithImageFile =
            Post.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithImageFile.getScheduledAt()
                    )
                    .build();

    public final static Post storedPostWithImageFile =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithImageFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .resources(
                            List.of(
                                    savedImageFileResource
                            )
                    )
                    .build();

    public final static Post savedPostWithImageFile =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithImageFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .build();

    public final static PostDto savedPostDtoWithImageFile = PostDto.builder()
            .id(EXISTENT_POST_ID)
            .authorId(EXISTENT_AUTHOR_ID)
            .content(correctContent)
            .scheduledAt(correctDraftWithImageFile.getScheduledAt())
            .createdAt(defaultCreateAt)
            .updatedAt(defaultCreateAt)
            .resources(
                    List.of(
                            new PreviewPostResourceDto(
                                    savedImageFileResource.getId(),
                                    savedImageFileResource.getName()
                            )
                    )
            )
            .commentsCount(0L)
            .likesCount(0L)
            .build();

    public final static DraftPostDto correctDraftWithAudioFile =
            DraftPostDto.builder()
                    .projectId(EXISTENT_PROJECT_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(defaultScheduledAt)
                    .resource(List.of(audioFile))
                    .build();

    public final static Post creatablePostWithAudioFile =
            Post.builder()
                    .projectId(EXISTENT_PROJECT_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithAudioFile.getScheduledAt()
                    )
                    .build();

    public final static Post storedPostWithAudioFile =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .projectId(EXISTENT_PROJECT_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithAudioFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .resources(
                            List.of(
                                    savedAudioFileResource
                            )
                    )
                    .build();

    public final static Post savedPostWithAudioFile =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .projectId(EXISTENT_PROJECT_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithAudioFile.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .build();

    public final static PostDto savedPostDtoWithAudioFile = PostDto.builder()
            .id(EXISTENT_POST_ID)
            .projectId(EXISTENT_PROJECT_ID)
            .authorId(1L)
            .content(correctContent)
            .scheduledAt(correctDraftWithAudioFile.getScheduledAt())
            .createdAt(defaultCreateAt)
            .updatedAt(defaultCreateAt)
            .resources(
                    List.of(
                            new PreviewPostResourceDto(
                                    savedAudioFileResource.getId(),
                                    savedAudioFileResource.getName()
                            )
                    )
            )
            .commentsCount(0L)
            .likesCount(0L)
            .build();

    public final static DraftPostDto correctDraftWithMultipleFiles =
            DraftPostDto.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(defaultScheduledAt)
                    .resource(List.of(textFile, imageFile, videoFile, audioFile))
                    .build();

    public final static Post creatablePostWithMultipleFile =
            Post.builder()
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithMultipleFiles.getScheduledAt()
                    )
                    .build();

    public final static Post storedPostWithMultipleFile =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithMultipleFiles.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .resources(
                            List.of(
                                    savedTextFileResource,
                                    savedImageFileResource,
                                    savedVideoFileResource,
                                    savedAudioFileResource
                            )
                    )
                    .build();

    public final static Post savedPostWithMultipleFile =
            Post.builder()
                    .id(EXISTENT_POST_ID)
                    .authorId(EXISTENT_AUTHOR_ID)
                    .content(correctContent)
                    .scheduledAt(
                            correctDraftWithMultipleFiles.getScheduledAt()
                    )
                    .createdAt(defaultCreateAt)
                    .updatedAt(defaultCreateAt)
                    .build();

    public final static PostDto savedPostDtoWithMultipleFiles = PostDto.builder()
            .id(EXISTENT_POST_ID)
            .authorId(EXISTENT_AUTHOR_ID)
            .content(correctContent)
            .scheduledAt(correctDraftWithMultipleFiles.getScheduledAt())
            .createdAt(defaultCreateAt)
            .updatedAt(defaultCreateAt)
            .resources(
                    List.of(
                            new PreviewPostResourceDto(
                                    savedTextFileResource.getId(),
                                    savedTextFileResource.getName()
                            ),
                            new PreviewPostResourceDto(
                                    savedImageFileResource.getId(),
                                    savedImageFileResource.getName()
                            ),
                            new PreviewPostResourceDto(
                                    savedVideoFileResource.getId(),
                                    savedVideoFileResource.getName()
                            ),
                            new PreviewPostResourceDto(
                                    savedAudioFileResource.getId(),
                                    savedAudioFileResource.getName()
                            )
                    )
            )
            .commentsCount(0L)
            .likesCount(0L)
            .build();

    public final static UpdatablePostDto updatePostContent = UpdatablePostDto.builder()
            .postId(EXISTENT_POST_ID)
            .content(newContent)
            .build();

    public final static Post postWithUpdatedContent = storedPostWithTextFile.toBuilder()
            .content(newContent)
            .build();

    public final static PostDto postDtoWithUpdatedContent = savedPostDtoWithTextFile.toBuilder()
            .content(newContent)
            .build();

    public final static UpdatablePostDto updateScheduleAtPost = UpdatablePostDto.builder()
            .postId(EXISTENT_POST_ID)
            .scheduledAt(newScheduleAt)
            .build();

    public final static Post postWithUpdatedScheduledAt = storedPostWithTextFile.toBuilder()
            .scheduledAt(newScheduleAt)
            .build();

    public final static PostDto postDtoWithUpdatedScheduledAt = savedPostDtoWithTextFile.toBuilder()
            .scheduledAt(newScheduleAt)
            .build();

    public final static UpdatablePostDto deleteScheduleAtPost = UpdatablePostDto.builder()
            .postId(EXISTENT_POST_ID)
            .deleteScheduledAt(true)
            .build();

    public final static Post postWithDeletedScheduledAt = storedPostWithTextFile.toBuilder()
            .scheduledAt(null)
            .build();

    public final static PostDto postDtoWithDeletedScheduledAt = savedPostDtoWithTextFile.toBuilder()
            .scheduledAt(null)
            .build();

    public final static UpdatablePostDto updateResourcePost = UpdatablePostDto.builder()
            .postId(EXISTENT_POST_ID)
            .resource(List.of(
                    new UpdatableResourceDto(
                            savedTextFileResource.getId(),
                            newTextFile
                    )
            ))
            .build();

    public final static Post postWithUpdatedResources = storedPostWithTextFile.toBuilder()
            .resources(List.of(savedNewTextFileResource))
            .build();

    public final static PostDto postDtoWithUpdatedResources = savedPostDtoWithTextFile.toBuilder()
            .resources(List.of(
                    new PreviewPostResourceDto(
                            savedNewTextFileResource.getId(),
                            savedNewTextFileResource.getName()
                    )
            ))
            .build();

    public final static Post alreadyPublishedPost = storedPostWithTextFile.toBuilder()
            .published(true)
            .build();

    public final static UpdatablePostDto updatableNonExistentPost = UpdatablePostDto.builder()
            .postId(NON_EXISTENT_ID)
            .build();

    public final static UpdatablePostDto updateMultipleFieldsPost = UpdatablePostDto.builder()
            .postId(EXISTENT_POST_ID)
            .content(TestData.newContent)
            .scheduledAt(TestData.newScheduleAt)
            .resource(List.of(
                    new UpdatableResourceDto(
                            null,
                            TestData.newVideFile
                    ),
                    new UpdatableResourceDto(
                            TestData.savedAudioFileResource.getId(),
                            TestData.newAudioFile
                    ),
                    new UpdatableResourceDto(
                            TestData.savedImageFileResource.getId(),
                            null
                    )
            ))
            .build();

    public final static Post storedPostWithMultipleUpdatableFields = Post.builder()
            .id(EXISTENT_POST_ID)
            .content(correctContent)
            .scheduledAt(defaultScheduledAt)
            .resources(List.of(
                    TestData.savedAudioFileResource,
                    TestData.savedImageFileResource
            ))
            .createdAt(defaultCreateAt)
            .build();

    public final static Post updatedPostWithMultipleFields = storedPostWithMultipleUpdatableFields.toBuilder()
            .id(EXISTENT_POST_ID)
            .content(updateMultipleFieldsPost.getContent())
            .scheduledAt(updateMultipleFieldsPost.getScheduledAt())
            .createdAt(defaultCreateAt)
            .resources(List.of(
                    TestData.savedNewVideoResource,
                    TestData.savedNewAudioFileResource
            ))
            .build();

    public final static PostDto updatedPostDtoWithMultipleFields = PostDto.builder()
            .id(EXISTENT_POST_ID)
            .content(updateMultipleFieldsPost.getContent())
            .scheduledAt(updateMultipleFieldsPost.getScheduledAt())
            .createdAt(defaultCreateAt)
            .resources(List.of(
                    new PreviewPostResourceDto(
                            TestData.savedNewVideoResource.getId(),
                            TestData.savedNewVideoResource.getName()
                    ),
                    new PreviewPostResourceDto(
                            TestData.savedNewAudioFileResource.getId(),
                            TestData.savedNewAudioFileResource.getName()
                    )
            ))
            .build();

    public final static Post createdProjectDraft1 = storedPostWithImageFile.toBuilder()
            .id(32L)
            .authorId(null)
            .projectId(EXISTENT_PROJECT_ID)
            .build();

    public final static PostDto createdProjectDraftPostDto1 = savedPostDtoWithImageFile.toBuilder()
            .id(32L)
            .authorId(null)
            .projectId(EXISTENT_PROJECT_ID)
            .build();

    public final static Post createdProjectDraft2 = storedPostWithVideo.toBuilder()
            .id(33L)
            .authorId(null)
            .projectId(EXISTENT_PROJECT_ID)
            .createdAt(createdProjectDraft1.getCreatedAt().plusHours(1))
            .build();

    public final static PostDto createdProjectDraftPostDto2 = savedPostDtoWithVideo.toBuilder()
            .id(33L)
            .authorId(null)
            .projectId(EXISTENT_PROJECT_ID)
            .createdAt(createdProjectDraft1.getCreatedAt().plusHours(1))
            .build();

    public final static Post publishedProjectPost1 = storedPostWithAudioFile.toBuilder()
            .id(34L)
            .published(true)
            .authorId(null)
            .publishedAt(defaultPublishedAt)
            .projectId(EXISTENT_PROJECT_ID)
            .build();

    public final static PostDto publishedProjectPostDto1 = savedPostDtoWithAudioFile.toBuilder()
            .id(34L)
            .authorId(null)
            .publishedAt(defaultPublishedAt)
            .projectId(EXISTENT_PROJECT_ID)
            .build();

    public final static Post publishedProjectPost2 = storedPostWithTextFile.toBuilder()
            .id(35L)
            .published(true)
            .authorId(null)
            .projectId(EXISTENT_PROJECT_ID)
            .publishedAt(defaultPublishedAt)
            .createdAt(publishedProjectPost1.getCreatedAt().plusDays(1))
            .build();

    public final static PostDto publishedProjectPostDto2 = savedPostDtoWithTextFile.toBuilder()
            .id(35L)
            .authorId(null)
            .projectId(EXISTENT_PROJECT_ID)
            .publishedAt(defaultPublishedAt)
            .createdAt(publishedProjectPost1.getCreatedAt().plusDays(1))
            .build();

    public final static Post createdAuthorDraft1 = storedPostWithImageFile.toBuilder()
            .id(36L)
            .authorId(EXISTENT_AUTHOR_ID)
            .projectId(null)
            .build();

    public final static PostDto createdAuthorDraftPostDto1 = savedPostDtoWithImageFile.toBuilder()
            .id(36L)
            .authorId(EXISTENT_AUTHOR_ID)
            .projectId(null)
            .build();

    public final static Post createdAuthorDraft2 = storedPostWithVideo.toBuilder()
            .id(37L)
            .authorId(EXISTENT_AUTHOR_ID)
            .projectId(null)
            .createdAt(createdProjectDraft1.getCreatedAt().plusHours(1))
            .build();

    public final static PostDto createdAuthorDraftPostDto2 = savedPostDtoWithVideo.toBuilder()
            .id(37L)
            .authorId(EXISTENT_AUTHOR_ID)
            .projectId(null)
            .createdAt(createdProjectDraft1.getCreatedAt().plusHours(1))
            .build();

    public final static Post publishedAuthorPost1 = storedPostWithAudioFile.toBuilder()
            .id(38L)
            .published(true)
            .authorId(EXISTENT_AUTHOR_ID)
            .publishedAt(defaultPublishedAt)
            .projectId(null)
            .build();

    public final static PostDto publishedAuthorPostDto1 = savedPostDtoWithAudioFile.toBuilder()
            .id(38L)
            .authorId(EXISTENT_AUTHOR_ID)
            .publishedAt(defaultPublishedAt)
            .projectId(null)
            .build();

    public final static Post publishedAuthorPost2 = storedPostWithTextFile.toBuilder()
            .id(39L)
            .published(true)
            .authorId(EXISTENT_AUTHOR_ID)
            .publishedAt(defaultPublishedAt)
            .projectId(null)
            .createdAt(publishedProjectPost1.getCreatedAt().plusDays(1))
            .build();

    public final static PostDto publishedAuthorPostDto2 = savedPostDtoWithTextFile.toBuilder()
            .id(39L)
            .authorId(EXISTENT_AUTHOR_ID)
            .publishedAt(defaultPublishedAt)
            .projectId(null)
            .createdAt(publishedProjectPost1.getCreatedAt().plusDays(1))
            .build();
}
