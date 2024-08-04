package faang.school.postservice.job.moderation;

public class SenderRunnable {

    public int id;
    public static int generateId = 0;

    public SenderRunnable() {
        id = generateId++;
    }

    public void apply() {

    }
}
