package com.grupo10.identity.infrastructure.config;

import com.grupo10.identity.application.port.in.RegistrarUsuarioUseCase;
import com.grupo10.identity.application.port.out.LoadUsuarioPort;
import com.grupo10.identity.domain.model.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Crea el usuario ADMIN por defecto en el primer arranque si no existe ninguno.
 * Credenciales configurables mediante variables de entorno (ver application.yml).
 */
@Component
public class AdminDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final LoadUsuarioPort loadUsuarioPort;
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@hospital.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.nombre:Administrador del Sistema}")
    private String adminNombre;

    public AdminDataInitializer(LoadUsuarioPort loadUsuarioPort,
                                RegistrarUsuarioUseCase registrarUsuarioUseCase) {
        this.loadUsuarioPort = loadUsuarioPort;
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (loadUsuarioPort.existsByUsername(adminUsername)) {
            log.info("Usuario admin '{}' ya existe — omitiendo inicialización", adminUsername);
            return;
        }

        var command = new RegistrarUsuarioUseCase.RegistrarUsuarioCommand(
                adminUsername, adminEmail, adminNombre, adminPassword, Set.of(Rol.ADMIN));
        registrarUsuarioUseCase.registrar(command);
        log.info("Usuario admin '{}' creado exitosamente", adminUsername);
    }
}
