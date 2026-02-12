package pl.patryk.cryptomarketranker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {
    @Bean
    public RestClient restClient(KangaProperties props) {

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(
                (int) props.http().connectTimeout().toMillis()
        );
        requestFactory.setReadTimeout(
                (int) props.http().readTimeout().toMillis()
        );

        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
