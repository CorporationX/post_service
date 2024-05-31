package faang.school.postservice.config.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("x-user-id");
        try {
            if (userId != null) {
                userContext.setUserId(Long.parseLong(userId));
                log.info("User id set to: {}", userId);
            }
            chain.doFilter(request, response);
        } catch (NumberFormatException e) {
            log.error("Failed to parse user id: {}", userId, e);
            throw new ServletException("Invalid user id format", e);
        } finally {
            userContext.clear();
            log.info("User id cleared");
        }
    }
}
