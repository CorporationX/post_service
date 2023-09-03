package faang.school.postservice.service.scheduledtaskactor;

import faang.school.postservice.dto.post.ScheduledTaskCompleteKeeper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTaskAndPostProcessingActor {

    private final PostSavingPreparerAfterScheduledTask preparingToSaveActor;
    private final PostRepository postRepository;
    private final ScheduledTaskAndPostUpdatesActor updatesActor;

    public void processScheduledTasks(List<ScheduledTask> tasks, int limit) {
        ScheduledTaskCompleteKeeper<Post> keeper = preparingToSaveActor.prepareToSave(tasks);
        postRepository.saveAll(keeper.getEntities()); // сохраняю изменения постов в бд
        updatesActor.checkUpdatedPostsAndSaveTask(keeper, limit);
    }
}
