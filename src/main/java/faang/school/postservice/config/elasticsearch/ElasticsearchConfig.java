package faang.school.postservice.config.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;


@Configuration
public class ElasticsearchConfig {
    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();

        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        ElasticsearchConverter elasticsearchConverter = new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());

        return new ElasticsearchRestTemplate(client, elasticsearchConverter);
    }
}