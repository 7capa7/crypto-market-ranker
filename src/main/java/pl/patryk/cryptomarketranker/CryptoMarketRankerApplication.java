package pl.patryk.cryptomarketranker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.patryk.cryptomarketranker.config.SecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties({SecurityProperties.class})
public class CryptoMarketRankerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoMarketRankerApplication.class, args);
    }

}
