package com.jporpha.fitsprint.config;

import com.jporpha.fitsprint.entity.Developer;
import com.jporpha.fitsprint.entity.Role;
import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.entity.Team;
import com.jporpha.fitsprint.entity.User;
import com.jporpha.fitsprint.repository.DeveloperRepository;
import com.jporpha.fitsprint.repository.SprintRepository;
import com.jporpha.fitsprint.repository.TeamRepository;
import com.jporpha.fitsprint.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Popula dados mínimos para uso manual em dev (H2). Não roda em prod
 * (fitsprint.seed.enabled=false em application-prod.yml).
 * Senha de todos os usuários seedados: "changeit".
 */
@Component
@ConditionalOnProperty(prefix = "fitsprint.seed", name = "enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final String SEED_PASSWORD = "changeit";

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final DeveloperRepository developerRepository;
    private final SprintRepository sprintRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(TeamRepository teamRepository, UserRepository userRepository,
                       DeveloperRepository developerRepository, SprintRepository sprintRepository,
                       PasswordEncoder passwordEncoder) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.developerRepository = developerRepository;
        this.sprintRepository = sprintRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (teamRepository.count() > 0) {
            return;
        }

        Team team = teamRepository.save(Team.builder().nome("Time FitSprint Demo").build());

        userRepository.save(User.builder()
                .email("super@fitsprint.local")
                .passwordHash(passwordEncoder.encode(SEED_PASSWORD))
                .role(Role.SUPER_ADMIN)
                .team(null)
                .build());

        userRepository.save(User.builder()
                .email("admin@fitsprint.local")
                .passwordHash(passwordEncoder.encode(SEED_PASSWORD))
                .role(Role.ADMIN)
                .team(team)
                .build());

        userRepository.save(User.builder()
                .email("dev@fitsprint.local")
                .passwordHash(passwordEncoder.encode(SEED_PASSWORD))
                .role(Role.USER)
                .team(team)
                .build());

        developerRepository.save(Developer.builder().nome("Ana (Tech Lead)").team(team).capacidadeTotal(13).build());
        developerRepository.save(Developer.builder().nome("Bruno (Sênior)").team(team).capacidadeTotal(21).build());
        developerRepository.save(Developer.builder().nome("Carla (Júnior)").team(team).capacidadeTotal(8).build());

        sprintRepository.save(Sprint.builder().nome("Sprint 1").team(team).build());
    }
}
