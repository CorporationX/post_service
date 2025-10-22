package faang.school.postservice.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(long projectId) {
        super("Project " + projectId + " not found");
    }
}