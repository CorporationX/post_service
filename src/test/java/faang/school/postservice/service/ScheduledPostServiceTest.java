package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.mapper.ScheduledTaskMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledEntityType;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.model.scheduled.ScheduledTaskStatus;
import faang.school.postservice.model.scheduled.ScheduledTaskType;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ScheduledTaskRepository;
import faang.school.postservice.service.scheduledtaskactor.ScheduledTaskAndPostProcessingActor;
import faang.school.postservice.util.exception.EntitySchedulingException;
import faang.school.postservice.util.exception.PostNotFoundException;
import faang.school.postservice.util.validator.ScheduledPostServiceValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Executor;

@ExtendWith(MockitoExtension.class)
class ScheduledPostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;

    @Mock
    private PostService postService;

    @Spy
    private ScheduledPostServiceValidator scheduledPostServiceValidator;

    @Mock
    private ScheduledTaskAndPostProcessingActor taskProcessingActor;

    @Mock
    private Executor taskExecutor;

    @Spy
    private ScheduledTaskMapperImpl scheduledTaskMapper;

    private ScheduledPostService scheduledPostService;

    @BeforeEach
    void setUp() {
        scheduledPostService = new ScheduledPostService(
                postService,
                scheduledTaskRepository,
                scheduledPostServiceValidator,
                scheduledTaskMapper,
                taskProcessingActor,
                taskExecutor,
                1000,
                3,
                100);
    }

    @Test
    void savePostBySchedule_PostIsAlreadyScheduled_ShouldThrowException() {
        ScheduledTaskDto dto = buildScheduledTaskDto();
        Mockito.when(postService.getPostById(1L)).thenReturn(buildPost());
        Mockito.when(scheduledTaskRepository.findPostById(1L)).thenReturn(Optional.of(buildScheduledTask()));

        EntitySchedulingException e = Assertions.assertThrows(EntitySchedulingException.class, () -> {
            scheduledPostService.savePostBySchedule(dto);
        });
        Assertions.assertEquals(String.format("Post with id = %d already scheduled", dto.entityId()), e.getMessage());
    }

    @Test
    void savePostBySchedule_ShouldMapCorrectly() {
        ScheduledTask actual = scheduledTaskMapper.toEntity(buildScheduledTaskDto());

        Assertions.assertEquals(buildScheduledTask(), actual);
    }

    @Test
    void savePostBySchedule_ShouldSave() {
        ScheduledTaskDto dto = buildScheduledTaskDto();
        Mockito.when(postService.getPostById(1L)).thenReturn(buildPost());
        Mockito.when(scheduledTaskRepository.findPostById(1L)).thenReturn(Optional.empty());

        scheduledPostService.savePostBySchedule(dto);

        Mockito.verify(scheduledTaskRepository).save(buildScheduledTask());
    }

    private Post buildPost() {
        return Post.builder()
                .id(0)
                .content("content")
                .authorId(1L)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .albums(new ArrayList<>())
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

    private ScheduledTask buildScheduledTask() {
        return ScheduledTask.builder()
                .entityType(ScheduledEntityType.POST)
                .taskType(ScheduledTaskType.PUBLISHING)
                .entityId(1L)
                .status(ScheduledTaskStatus.NEW)
                .build();
    }

    private ScheduledTaskDto buildScheduledTaskDto() {
        return ScheduledTaskDto.builder()
                .entityType(ScheduledEntityType.POST)
                .taskType(ScheduledTaskType.PUBLISHING)
                .entityId(1L)
                .status(ScheduledTaskStatus.NEW)
                .build();
    }
}
