package com.example.Gestor.Config;

import com.example.Gestor.Config.Security.*;
import com.example.Gestor.Model.Entity.RoleType;
import com.example.Gestor.Model.Service.Usuario.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private IUsuarioService userService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JWTEntryPoint unauthorizedHandler;

    @Autowired
    private JWTAccesDenied accessDeniedHandler;

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userService.loadUserByUsername(username);
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET","POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                //Admin
                                .requestMatchers(HttpMethod.DELETE, "/api/borrar/{id}").hasRole(RoleType.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/api/listar").hasAnyRole(RoleType.ADMIN.name(),RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/perfil/{id}").hasAnyRole(RoleType.ADMIN.name(),RoleType.USER.name())
                                //.requestMatchers(HttpMethod.PUT, "/api/Perfil_Modificar_Admin/{id}").hasRole(RoleType.ADMIN.name())
                                //User

                                //Funcionan

                                .requestMatchers(HttpMethod.PUT, "/api/crearCuenta").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.POST, "/api/Create_Transaccion").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.PUT, "/api/Perfil_Modificar_User/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.POST, "/api/crearPresupuesto").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.DELETE, "/api/presupuestoBorrar/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/presupuesto/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/usuarioPresupuestos/{idUser}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "api/porcentaje-gastado/{usuarioId}").hasAnyRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.PUT, "/api/Presupuesto_Modi/{id}").hasAnyRole(RoleType.USER.name())
                                //.requestMatchers(HttpMethod.GET, "/api/limite/{idUser}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.PUT, "/api/categoriaModificar/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/transacciones/{id_User}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/transaccionesByCategoria/{id_User}/{categoriaId}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/total-gastos/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/total-ingresos/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.PUT, "/api/TransaccionModificar/{id}").hasRole(RoleType.USER.name())
                                //.requestMatchers(HttpMethod.DELETE, "/api/transaccionBorrar/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/cuentaUser/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/saldo/{usuarioId}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/presupuestoactual/{perfilId}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/telefonouser/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/perfilid").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/auditoria/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/usuarioauditrorias/{idUser}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/transaccion/{id}").hasRole(RoleType.USER.name())
                                .requestMatchers(HttpMethod.GET, "/api/tiene-cuenta/{usuarioId}").hasRole(RoleType.USER.name())
                                //No agregar
                                .requestMatchers(HttpMethod.GET, "api/buscar/{perfilId}/{fecha}").hasRole(RoleType.USER.name())
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
