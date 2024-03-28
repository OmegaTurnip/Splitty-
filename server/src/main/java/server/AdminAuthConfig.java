package server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import server.api.AdminController;
@Configuration
public class AdminAuthConfig {

    /**
     * Makes a security framework that is password protected, so that
     * only a request where the user has the role ADMIN will be able
     * to access the functionality. Otherwise, a 401 unautorized header
     * will be returned.
     * @param httpSecurity the security protocol
     * @return a security filter
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity)
            throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }

    /**
     * Gives the ADMIN role and prints the username and password of a user
     * to the server terminal.
     * @return UserDetailsService containing all users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin =
                User.builder().username("admin")
                        .password("{noop}"+AdminController.getPassword())
                        .roles("ADMIN")
                        .build();
        System.out.println("user: "+admin.getUsername());
        System.out.println("password: "+admin.getPassword());
        return new InMemoryUserDetailsManager(admin);
    }
}