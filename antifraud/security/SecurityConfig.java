package antifraud.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handle auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .antMatchers("/actuator/shutdown").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/user/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/antifraud/transaction/**").hasRole("MERCHANT")
                .antMatchers(HttpMethod.GET, "/api/auth/list/**").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                .antMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.PUT, "/api/auth/access/**").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.PUT, "/api/auth/role/**").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip/**").hasRole("SUPPORT")
                .antMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip/**").hasRole("SUPPORT")
                .antMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/**").hasRole("SUPPORT")
                .antMatchers(HttpMethod.POST, "/api/antifraud/stolencard/**").hasRole("SUPPORT")
                .antMatchers(HttpMethod.GET, "/api/antifraud/stolencard/**").hasRole("SUPPORT")
                .antMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/**").hasRole("SUPPORT")
                .antMatchers(HttpMethod.GET, "/api/antifraud/history/**").hasRole("SUPPORT")
                .antMatchers(HttpMethod.PUT, "/api/antifraud/transaction/**").hasRole("SUPPORT")

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

}