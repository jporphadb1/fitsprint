package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.repository.BolinaRepository;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Ocupação de um developer = soma dos story points de todas as bolinas atribuídas a
 * ele no sprint ativo, independentemente do estado, desde que não estejam fora nem
 * eliminadas. Nunca persistida — recalculada em tempo de consulta. Reutilizado pelos
 * módulos Capacidade do time e Ocupação e avanço.
 */
@Component
public class OcupacaoCalculator {

    private final BolinaRepository bolinaRepository;

    public OcupacaoCalculator(BolinaRepository bolinaRepository) {
        this.bolinaRepository = bolinaRepository;
    }

    /** Ocupação por developer_id no sprint informado. Developer sem tarefas simplesmente não aparece no mapa. */
    public Map<Long, Integer> calcularOcupacaoPorDeveloper(Long sprintId) {
        return bolinaRepository.findAllBySprintIdAndEliminadoFalse(sprintId).stream()
                .filter(b -> !Boolean.TRUE.equals(b.getFuera()))
                .filter(b -> b.getDeveloper() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getDeveloper().getId(),
                        Collectors.summingInt(b -> b.getTamanho() == null ? 0 : b.getTamanho())));
    }
}
