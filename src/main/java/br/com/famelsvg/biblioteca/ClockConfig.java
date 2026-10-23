package br.com.famelsvg.biblioteca;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {
    // Bean separado para os testes poderem trocar o relógio.
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
