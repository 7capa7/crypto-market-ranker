package pl.patryk.cryptomarketranker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Configuration
public class ExecutorConfig {

    @Bean(destroyMethod = "shutdown")
    ExecutorService spreadExecutor() {
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("spread-vt-", 0).factory());
    }

    @Bean
    Semaphore kangaConcurrencyLimiter(KangaProperties props) {
        return new Semaphore(Math.max(1, props.concurrencyLimit()), true);
    }
}
