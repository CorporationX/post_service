package faang.school.postservice.events;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Data
public class FeedUpdateEvent {
    private List<Long> usersIds = new ArrayList<>();

    private long postId;

    public void addUser(Long id) {
        usersIds.add(id);
    }

    public boolean listIsFull() {
        return usersIds.size() == 100;
    }

    public void clearList(){
        usersIds.clear();
    }
}
