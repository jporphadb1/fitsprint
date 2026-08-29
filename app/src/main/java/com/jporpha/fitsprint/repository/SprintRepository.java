package com.jporpha.fitsprint.repository;

import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.entity.SprintStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintRepository extends JpaRepository<Sprint, Long> {

    Optional<Sprint> findByTeamIdAndStatus(Long teamId, SprintStatus status);

    List<Sprint> findAllByTeamId(Long teamId);

    Optional<Sprint> findByIdAndTeamId(Long id, Long teamId);
}
