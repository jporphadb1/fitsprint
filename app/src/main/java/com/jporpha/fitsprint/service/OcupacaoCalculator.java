package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.repository.BolinaRepository;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Ocupação de um developer = soma dos story points de todas as bolinas atribuídas a
 * ele no sprint ativo, independentemente do estado, desde que não estejam fora nem
 * eliminadas. Nunca persistida — recalculada em tempo de consulta. Reutilizado pelos
 * módulos Capacidade do time, Ocupação e avanço, e Filtros e vistas.
 */
@Component
public class OcupacaoCalculator {

    public static final Ocupacao VAZIA = new Ocupacao(0, 0);

    private final BolinaRepository bolinaRepository;

    public OcupacaoCalculator(BolinaRepository bolinaRepository) {
        this.bolinaRepository = bolinaRepository;
    }

    /** Story points e quantidade de tarefas por developer_id no sprint informado. */
    public Map<Long, Ocupacao> calcularOcupacaoPorDeveloper(Long sprintId) {
        return bolinaRepository.findAllBySprintIdAndEliminadoFalse(sprintId).stream()
                .filter(b -> !Boolean.TRUE.equals(b.getFuera()))
                .filter(b -> b.getDeveloper() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getDeveloper().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), bolinas -> new Ocupacao(
                                bolinas.stream().mapToInt(b -> b.getTamanho() == null ? 0 : b.getTamanho()).sum(),
                                bolinas.size()))));
    }

    public record Ocupacao(int storyPoints, int quantidadeTarefas) {
    }
}
