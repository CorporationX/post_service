package faang.school.postservice.service;

import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.mapper.ScheduledTaskMapper;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.model.scheduled.ScheduledTaskStatus;
import faang.school.postservice.repository.ScheduledTaskRepository;
import faang.school.postservice.service.scheduledtaskactor.ScheduledTaskAndPostProcessingActor;
import faang.school.postservice.util.validator.ScheduledPostServiceValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ScheduledPostService {

    private final PostService postService;
    private final ScheduledTaskRepository scheduledTaskRepository;
    private final ScheduledPostServiceValidator validator;
    private final ScheduledTaskMapper scheduledTaskMapper;
    private final ScheduledTaskAndPostProcessingActor taskProcessingActor;
    private final Executor taskExecutor;
    private final int requestSize;
    private final int limit;
    private final int batchSize;

    @Autowired
    public ScheduledPostService(PostService postService, ScheduledTaskRepository scheduledTaskRepository,
                                ScheduledPostServiceValidator validator, ScheduledTaskMapper scheduledTaskMapper,
                                ScheduledTaskAndPostProcessingActor taskProcessingActor,
                                @Qualifier("taskExecutor") Executor taskExecutor, @Value("${requestSize}") int requestSize,
                                @Value("${schedule.retry_limit}") int limit, @Value("${batchSize}")int batchSize) {
        this.postService = postService;
        this.scheduledTaskRepository = scheduledTaskRepository;
        this.validator = validator;
        this.scheduledTaskMapper = scheduledTaskMapper;
        this.taskProcessingActor = taskProcessingActor;
        this.taskExecutor = taskExecutor;
        this.requestSize = requestSize;
        this.limit = limit;
        this.batchSize = batchSize;
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public ScheduledTaskDto savePostBySchedule(ScheduledTaskDto dto) {
        postService.getPostById(dto.entityId());
        Optional<ScheduledTask> scheduledPostById = scheduledTaskRepository.findPostById(dto.entityId());

        validator.validateToActWithPostBySchedule(scheduledPostById);

        ScheduledTask task = scheduledTaskMapper.toEntity(dto);
        task.setStatus(ScheduledTaskStatus.NEW);

        ScheduledTask saved = scheduledTaskRepository.save(task);

        log.info("Entity type: {}, task type: {} were saved", dto.entityType(), dto.taskType());

        return scheduledTaskMapper.toDto(saved);
    }

    @Transactional
    public void completeScheduledPosts() {
        List<ScheduledTask> tasks = scheduledTaskRepository.findNewOrErrorPostsToPublishOrDelete(requestSize);

        if (!tasks.isEmpty()) {
            for (int i = 0; i <= tasks.size(); i += batchSize + 1) {
                List<ScheduledTask> batch = tasks.subList(i, Math.min(tasks.size(), i + batchSize));

                taskExecutor.execute(() -> taskProcessingActor.processScheduledTasks(batch, limit));
            }
        }
    }
}
