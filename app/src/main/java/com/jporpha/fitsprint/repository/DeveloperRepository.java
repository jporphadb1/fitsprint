package com.jporpha.fitsprint.repository;

import com.jporpha.fitsprint.entity.Developer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    List<Developer> findAllByTeamId(Long teamId);

    Optional<Developer> findByIdAndTeamId(Long id, Long teamId);

    @Query("select coalesce(sum(d.capacidadeTotal), 0) from Developer d where d.team.id = :teamId")
    Integer sumCapacidadeTotalByTeamId(@Param("teamId") Long teamId);
}
