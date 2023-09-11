package faang.school.postservice.config.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ProjectHeaderFilter implements Filter {

    private final ProjectContext projectContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String projectId = req.getHeader("x-project-id");
        if (projectId != null) {
            projectContext.setProjectId(Long.parseLong(projectId));
        }
        try {
            chain.doFilter(request, response);
        } finally {
            projectContext.clear();
        }
    }
}
