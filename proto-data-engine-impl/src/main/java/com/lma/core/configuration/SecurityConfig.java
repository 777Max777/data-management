package com.lma.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig /*extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter, CsrfFilter.class);

        http
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,
                        "/management/**", "/explorer/**").permitAll()
                .antMatchers(HttpMethod.GET, "/management/**", "/explorer/**").permitAll()
                .antMatchers(HttpMethod.PUT,
                        "/management/**", "/explorer/**").permitAll()
                .and()
                .csrf().disable();

        http.cors();
    }
    */ {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((authz) -> authz
                        .anyRequest().permitAll()
//                        .antMatchers(HttpMethod.POST,
//                            "/management/**", "/explorer/**").permitAll()
//                        .antMatchers(HttpMethod.GET, "/management/**", "/explorer/**").permitAll()
//                        .antMatchers(HttpMethod.PUT,
//                                "/management/**", "/explorer/**").permitAll()
                )
                .httpBasic(withDefaults())
                .cors();

        return http.build();
    }

}
