    package com.ayoub.orders.config;

    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AbstractAuthenticationToken;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.core.authority.AuthorityUtils;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

    @Configuration
    public class SecurityConfig {

        private static final String API_KEY = "secret-api-key";

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            ApiKeyAuthFilter filter = new ApiKeyAuthFilter();

            http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(filter, AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/api/v1/hello"
                    ).permitAll()
                    .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

            return http.build();
        }

        static class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

            @Override
            protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
                String key = request.getHeader("X-API-KEY");
                if (!API_KEY.equals(key)) {
                    return null;
                }
                return new AbstractAuthenticationToken(
                        AuthorityUtils.createAuthorityList("ROLE_API")) {
                    @Override public Object getCredentials() { return ""; }
                    @Override public Object getPrincipal() { return "api-key"; }
                };
            }

            @Override
            protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
                return "";
            }
        }
    }
