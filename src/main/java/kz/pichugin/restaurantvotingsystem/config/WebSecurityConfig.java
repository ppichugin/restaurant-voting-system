package kz.pichugin.restaurantvotingsystem.config;

import kz.pichugin.restaurantvotingsystem.model.Role;
import kz.pichugin.restaurantvotingsystem.model.User;
import kz.pichugin.restaurantvotingsystem.repository.UserRepository;
import kz.pichugin.restaurantvotingsystem.web.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static kz.pichugin.restaurantvotingsystem.util.UserUtil.PASSWORD_ENCODER;

@Configuration
@EnableWebSecurity
@Slf4j
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    // https://stackoverflow.com/a/70176629/19127801
    @Bean
    @Override
    // https://stackoverflow.com/a/70176629/548473
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(
                        email -> {
                            log.debug("Authenticating '{}'", email);
                            Optional<User> optionalUser = userRepository.getByEmail(email.toLowerCase());
                            return new AuthUser(optionalUser.orElseThrow(
                                    () -> new UsernameNotFoundException("User '" + email + "' was not found")));
                        })
                .passwordEncoder(PASSWORD_ENCODER);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST, "/api/profile").anonymous()
                .antMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();
    }
}