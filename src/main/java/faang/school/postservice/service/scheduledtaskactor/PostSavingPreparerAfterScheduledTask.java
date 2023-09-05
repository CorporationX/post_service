package faang.school.postservice.service.scheduledtaskactor;

import faang.school.postservice.dto.post.ScheduledTaskCompleteKeeper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostSavingPreparerAfterScheduledTask {

    private final ScheduledTaskAndPostFieldsSettingActor fieldsSettingActor;
    private final PostService postService;

    public ScheduledTaskCompleteKeeper<Post> prepareToSave(List<ScheduledTask> tasks) {
        List<Post> postsToSave = new LinkedList<>();

        tasks.forEach(task -> {
            Post post = postService.getPostById(task.getEntityId()); // каждому таску соответствует какой-то пост, эти посты из бд складываю в список, чтобы потом проверить, обновились ли они
            fieldsSettingActor.setFieldsForScheduledPost(post, task);
            postsToSave.add(post);
        });

        return new ScheduledTaskCompleteKeeper<>(postsToSave, tasks);
    }
}
