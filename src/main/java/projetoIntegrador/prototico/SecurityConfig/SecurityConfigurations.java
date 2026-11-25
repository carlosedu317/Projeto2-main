
package projetoIntegrador.prototico.SecurityConfig;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import projetoIntegrador.prototico.model.TipoUsuario;
import projetoIntegrador.prototico.model.Usuario;
import projetoIntegrador.prototico.repository.UsuarioRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(req -> {

                                        // ROTAS PÚBLICAS
                                        req.requestMatchers(
                                                        "/swagger-ui/**",
                                                        "/swagger-ui.html",
                                                        "/v3/api-docs/**").permitAll();

                                        req.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                                        req.requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll();
                                        req.requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll();

                                        // PRODUTOS: VENDEDOR e ADMIN
                                        req.requestMatchers(HttpMethod.POST, "/api/produtos/**")
                                                        .hasAnyRole("VENDEDOR", "ADMIN");

                                        req.requestMatchers(HttpMethod.PUT, "/api/produtos/**")
                                                        .hasAnyRole("VENDEDOR", "ADMIN");

                                        req.requestMatchers(HttpMethod.DELETE, "/api/produtos/**")
                                                        .hasAnyRole("VENDEDOR", "ADMIN");

                                        // CARRINHO: CLIENTE, VENDEDOR e ADMIN
                                        req.requestMatchers(HttpMethod.GET, "/api/carrinho/**")
                                                        .hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN");

                                        req.requestMatchers(HttpMethod.POST, "/api/carrinho/**")
                                                        .hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN");

                                        req.requestMatchers(HttpMethod.PUT, "/api/carrinho/**")
                                                        .hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN");

                                        req.requestMatchers(HttpMethod.DELETE, "/api/carrinho/**")
                                                        .hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN");

                                        // PAGAMENTOS: CLIENTE
                                        req.requestMatchers(HttpMethod.POST, "/api/pagamentos/**")
                                                        .hasRole("CLIENTE");

                                        // CLIENTE PODE SE TORNAR VENDEDOR
                                        req.requestMatchers(HttpMethod.PATCH, "/api/usuarios/tornar-vendedor")
                                                        .hasRole("CLIENTE");

                                        // ADMINISTRAÇÃO: SOMENTE ADMIN
                                        req.requestMatchers(HttpMethod.GET, "/api/usuarios")
                                                        .hasRole("ADMIN");

                                        req.requestMatchers(HttpMethod.DELETE, "/api/usuarios/**")
                                                        .hasRole("ADMIN");

                                        req.requestMatchers(HttpMethod.DELETE, "/api/pagamentos/**")
                                                        .hasRole("ADMIN");

                                                        // ADMINISTRAÇÃO: SOMENTE ADMIN
                                        req.requestMatchers(
                                                        HttpMethod.GET,
                                                        "/api/usuarios",
                                                        "/api/usuarios/**").hasRole("ADMIN");

                                        req.requestMatchers(
                                                        HttpMethod.PUT,
                                                        "/api/usuarios/**").hasRole("ADMIN");

                                        req.requestMatchers(
                                                        HttpMethod.DELETE,
                                                        "/api/usuarios/**").hasRole("ADMIN");

                                        // DEMAIS ROTAS EXIGEM AUTENTICAÇÃO
                                        req.anyRequest().authenticated();

                                })

                                // Desativa autenticação HTTP Basic
                                .httpBasic(httpBasic -> httpBasic.disable())

                                .build();
        }

        // CONFIGURAÇÃO CORS
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOriginPatterns(List.of(
                                "http://localhost:*",
                                "http://127.0.0.1:*"));

                config.setAllowedMethods(List.of(
                                "GET",
                                "POST",
                                "PUT",
                                "PATCH",
                                "DELETE",
                                "OPTIONS"));

                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", config);

                return source;
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {

                return configuration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
