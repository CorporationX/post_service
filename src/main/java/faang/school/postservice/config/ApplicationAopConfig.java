package faang.school.postservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("faang.school.postservice.publisher")
@EnableAspectJAutoProxy
public class ApplicationAopConfig {
}
