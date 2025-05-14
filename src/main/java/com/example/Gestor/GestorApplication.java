package com.example.Gestor;

import com.example.Gestor.Model.Entity.Rol;
import com.example.Gestor.Model.Entity.RoleType;
import com.example.Gestor.Model.Entity.Perfil;

import com.example.Gestor.Model.Repository.IRepositoryPerfil;
import com.example.Gestor.Model.Repository.IUsuarioRepository.IRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class GestorApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestorApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner initAdmin(
			IRepositoryPerfil perfilRepository,
			IRolRepository rolRepository) {

		return args -> {
			// Configurar password encoder
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

			// Verificar si el perfil admin ya existe
			if (perfilRepository.findByUsername("admin").isEmpty()) {
				System.out.println("Creando perfil de usuario administrador...");

				// Crear o obtener rol ADMIN
				Rol adminRole = rolRepository.findByNombre(RoleType.ADMIN)
						.orElseGet(() -> {
							Rol newRole = new Rol();
							newRole.setNombre(RoleType.ADMIN);
							return rolRepository.save(newRole);
						});

				// Crear perfil admin (hereda de Usuario)
				Perfil perfilAdmin = new Perfil();
				perfilAdmin.setUsername("admin");
				perfilAdmin.setPassword(passwordEncoder.encode("admin"));
				perfilAdmin.setActivo(false); // Asegurar que pueda acceder
				perfilAdmin.setRoles(List.of(adminRole));

				// Datos personales del admin
				perfilAdmin.setNombre("Administrador");
				perfilAdmin.setApellido("Sistema");
				perfilAdmin.setDni("00000000");
				perfilAdmin.setCreationDate(LocalDateTime.now());
				perfilAdmin.setEmail("admin@system.com");
				perfilAdmin.setTelefono("000000000");

				// Guardar perfil
				perfilRepository.save(perfilAdmin);
				System.out.println("Perfil admin creado exitosamente");
			} else {
				System.out.println("El perfil admin ya existe en el sistema");
			}
		};
	}
}
