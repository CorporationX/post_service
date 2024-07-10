package faang.school.postservice.integration.service;

import faang.school.postservice.integration.IntegrationTestBase;
import faang.school.postservice.service.FeedHeaterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = "test")
@DirtiesContext
public class FeedHeaterIntegrationTest extends IntegrationTestBase {

    @Autowired
    private FeedHeaterService feedHeaterService;

    @Test
    public void feedHeaterService() {
        feedHeaterService.feedHeat();
    }
}
