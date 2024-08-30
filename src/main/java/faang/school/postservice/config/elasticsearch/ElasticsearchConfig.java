package faang.school.postservice.config.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.data.elasticsearch.rest.uris}")
    private String elasticsearchUris;

    @Bean
    public ElasticsearchClient elasticsearchClient(ObjectMapper objectMapper) {
        RestClient restClient = RestClient.builder(
                HttpHost.create(elasticsearchUris)
        ).build();

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(objectMapper)
        );

        return new ElasticsearchClient(transport);
    }
}