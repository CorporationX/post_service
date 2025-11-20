package faang.school.postservice.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Video {
    private String name = "Some name";
    private AtomicLong likes = new AtomicLong(0);
    private String author = "";
    private Long views = 0L;
    private Category category;

    public void addLike() {
        synchronized(likes) {
             likes.getAndAdd(1);
             System.out.println("Vars: " + name + " " + likes + " " + author + " " + views + " " + category);
        }
    }

    public Video(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        List<Video> videos = new ArrayList<>();
        Map<Category, List<Video>> categoryToVideos = new ConcurrentHashMap<>();

        for (Video video : videos) {
            categoryToVideos.computeIfAbsent(video.category, (k) -> new CopyOnWriteArrayList<>()).add(video);
        }
    }
}
