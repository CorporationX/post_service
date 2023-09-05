package faang.school.postservice.service.scheduledtaskactor;

import faang.school.postservice.dto.post.ScheduledTaskCompleteKeeper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.repository.ScheduledTaskRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTaskAndPostUpdatesActor {

    private final ScheduledTaskAndPostCheckerActor correspondenceActor;
    private final PostService postService;
    private final ScheduledTaskRepository scheduledTaskRepository;

    public void checkUpdatedPostsAndSaveTask(ScheduledTaskCompleteKeeper<Post> scheduledTaskCompleteKeeper, int limit) {
        List<Post> posts = scheduledTaskCompleteKeeper.getEntities();
        List<ScheduledTask> tasks = scheduledTaskCompleteKeeper.getScheduledTasks();

        for (int i = 0; i < posts.size(); i++) { // проверяю обновлись ли посты, если нет, то ставлю статус таскам "ERROR" или "FAILURE"
            Post foundPost = postService.getPostById(posts.get(i).getId()); // id поста из списка
            ScheduledTask task = tasks.get(i); // id таска из списка

            correspondenceActor.actWithCorrespondence(foundPost, task, limit);

            scheduledTaskRepository.save(task); // сохраняю состояние таска в бд
        }
    }
}
