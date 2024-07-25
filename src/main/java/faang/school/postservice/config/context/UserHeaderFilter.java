package faang.school.postservice.config.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        String userId = req.getHeader("x-user-id");

        if (userId != null) {
            try {
                if (Long.parseLong(userId) > 0) {
                    userContext.setUserId(Long.parseLong(userId));
                    chain.doFilter(request, response);
                }
                else throw new IllegalArgumentException("Invalid user id");

            } catch (NumberFormatException e) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().println("Invalid 'x-user-id' format'");
            } finally {
                userContext.clear();
            }
        }
        else {
            throw new IllegalArgumentException("Missing required header 'x-user-id'. Please include 'x-user-id' header with a valid user ID in your request.");
        }
    }
}
