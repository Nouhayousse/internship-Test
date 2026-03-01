package pfa.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reservationApiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reservation API")
                        .description("API de gestion de réservations de ressources partagées")
                        .version("1.0.0")
                );
    }
}
