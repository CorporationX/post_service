package faang.school.postservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableAsync
@EnableSwagger2
@EnableFeignClients(basePackages = "faang.school.postservice.client")
public class PostServiceApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    public Docket apis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("faang.school.postservice"))
                .build();
    }
}
