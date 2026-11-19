package cl.duoc.sonidovivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;


@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class SonidoVivoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonidoVivoApplication.class, args);
    }
}
