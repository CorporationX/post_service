package faang.school.postservice.config.filter;


import faang.school.postservice.filter.UserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final UserFilter userFilter;

    public FilterConfig(UserFilter userFilter) {
        this.userFilter = userFilter;
    }

    @Bean
    public FilterRegistrationBean<UserFilter> registerUserFilter() {
        FilterRegistrationBean<UserFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(userFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
