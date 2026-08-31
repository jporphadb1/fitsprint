package com.jporpha.fitsprint.repository;

import com.jporpha.fitsprint.entity.Bolina;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BolinaRepository extends JpaRepository<Bolina, Long> {

    Optional<Bolina> findByIdAndTeamId(Long id, Long teamId);

    List<Bolina> findAllBySprintIdAndEliminadoFalse(Long sprintId);
}
