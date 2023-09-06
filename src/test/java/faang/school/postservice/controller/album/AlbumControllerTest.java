package faang.school.postservice.controller.album;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import faang.school.postservice.WireMockConfig;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@EnableConfigurationProperties
@ContextConfiguration(classes = { WireMockConfig.class })
class AlbumControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WireMockServer mockBooksService;

    @Autowired
    private UserServiceClient userServiceClient;
    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");


    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        GetUserMock.setupMockBooksResponse(mockBooksService);
//        mockBooksService.start();
//        mockBooksService = new WireMockServer(9561);
//        WireMock.configureFor("localhost", mockBooksService.port());
    }

    @Test
    public void createAlbumTest() throws Exception {
        mockMvc.perform(post("/album/create")
                        .header("x-user-id", "1")
                        .contentType("application/json")
                        .content("{\"title\":\"test332\"," +
                                "\"description\":\"test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void createAlbumWithEmptyTitleTest() throws Exception {
        mockMvc.perform(post("/album/create")
                        .header("x-user-id", "1")
                        .contentType("application/json")
                        .content("{\"description\":\"test\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAlbumWithEmptyDescriptionTest() throws Exception {
        mockMvc.perform(post("/album/create")
                        .header("x-user-id", "1")
                        .contentType("application/json")
                        .content("{\"title\":\"test\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAlbumWithWrongUserIdTest() throws Exception {
        mockMvc.perform(post("/album/create")
                        .header("x-user-id", "43")
                        .contentType("application/json")
                        .content("{\"title\":\"test\"," +
                                "\"description\":\"test\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteAlbumTest() throws Exception {
        mockMvc.perform(delete("/album/delete/1")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAlbumWithWrongAuthorTest() throws Exception {
        mockMvc.perform(delete("/album/delete/1")
                        .header("x-user-id", "14"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addPostToAlbumTest() throws Exception {
        mockMvc.perform(post("/album/addPost/2/1")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void addPostToAlbumWithWrongAuthorTest() throws Exception {
        mockMvc.perform(post("/album/addPost/1/1")
                        .header("x-user-id", "12"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addToFavoriteTest() throws Exception {
        mockMvc.perform(post("/album/addToFavorite/3")
                        .header("x-user-id", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteFromFavoriteTest() throws Exception {

        mockMvc.perform(post("/album/addToFavorite/3").header("x-user-id", "1"));

        mockMvc.perform(delete("/album/removeFromFavorite/3")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAlbumTest() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/album/get/2"))
                .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        Album album = mapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), Album.class);
        assertEquals("test2", album.getTitle());
    }

    @Test
    public void getAlbumWithWrongIdTest() throws Exception {
        mockMvc.perform(get("/album/get/66"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllUserAlbumsTest() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/album/get/allUserAlbums")
                        .header("x-user-id", "3")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        List<Album> albums = mapper.readValue(resultActions.andReturn().getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Album.class));
        assertEquals(2, albums.size());
    }

    @Test
    public void getAllUserAlbumsWithTitleFilterTest() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/album/get/allUserAlbums")
                        .header("x-user-id", "3")
                        .contentType("application/json")
                        .content("{\"titlePattern\":\"test\"}"))
                .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        List<Album> albums = mapper.readValue(resultActions.andReturn().getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Album.class));
        assertEquals(2, albums.size());
    }

    @Test
    public void getAllAlbumsTest() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/album/get/allAlbums")
                        .header("x-user-id", "3")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        List<Album> albums = mapper.readValue(resultActions.andReturn().getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Album.class));
        assertEquals(6, albums.size());
    }

    @Test
    public void getAllFavoritesTest() throws Exception {
        mockMvc.perform(post("/album/addToFavorite/4").header("x-user-id", "2"));
        ResultActions resultActions = mockMvc.perform(get("/album/get/allFavorites")
                        .header("x-user-id", "2")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        List<Album> albums = mapper.readValue(resultActions.andReturn().getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Album.class));
        assertEquals(1, albums.size());
    }

    @Test
    public void updateAlbumTest() throws Exception {
        mockMvc.perform(post("/album/update")
                        .header("x-user-id", "1")
                        .contentType("application/json")
                        .content("{\"title\":\"new_test\"," +
                                "\"description\":\"test\"}"))
                .andExpect(status().isOk());
    }
}