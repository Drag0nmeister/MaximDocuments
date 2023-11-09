package documents.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public SpringFXMLLoader springFXMLLoader(ApplicationContext applicationContext) {
        return new SpringFXMLLoader(applicationContext);
    }
}
