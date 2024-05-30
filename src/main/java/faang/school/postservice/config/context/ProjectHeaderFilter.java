package faang.school.postservice.config.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProjectHeaderFilter implements Filter {

    private final ProjectContext projectContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String projectId = req.getHeader("x-project-id");
        try {
            if (projectId != null) {
                projectContext.setProjectId(Long.parseLong(projectId));
                log.info("Project ID set to: {}", projectId);
            }
            chain.doFilter(request, response);
        } catch (NumberFormatException e) {
            log.error("Failed to parse project ID: {}", projectId, e);
            throw new ServletException("Invalid project ID format", e);
        } finally {
            projectContext.clear();
            log.info("Project ID cleared");
        }
    }
}