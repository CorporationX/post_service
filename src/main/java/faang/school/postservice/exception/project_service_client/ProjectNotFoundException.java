package faang.school.postservice.exception.project_service_client;

import java.util.NoSuchElementException;

public class ProjectNotFoundException extends NoSuchElementException {
    public ProjectNotFoundException(String msg) {
        super(msg);
    }

    public ProjectNotFoundException(long projectId) {
        super(String.format("Project with id %d not found", projectId));
    }
}
