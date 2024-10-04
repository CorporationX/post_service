package faang.school.postservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PostCorrector {

    @Autowired
    @Qualifier("getCorrectorClient")
    private final WebClient correctorClient;
}
