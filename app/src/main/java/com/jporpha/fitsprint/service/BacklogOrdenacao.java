package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.entity.Bolina;
import java.util.Comparator;

/**
 * Regra de ordenação do backlog priorizado: prioridade final manual primeiro
 * (quando existir), depois ratio decrescente, com data de criação como
 * desempate final. Reutilizada pelos módulos Backlog priorizado e Filtros e vistas.
 */
public final class BacklogOrdenacao {

    private BacklogOrdenacao() {
    }

    public static Comparator<Bolina> porPrioridade() {
        return Comparator
                .<Bolina>comparingInt(b -> b.getPrioridadeFinal() != null ? 0 : 1)
                .thenComparingInt(b -> b.getPrioridadeFinal() != null ? b.getPrioridadeFinal() : 0)
                .thenComparing(Comparator.comparingDouble(Bolina::getRatio).reversed())
                .thenComparing(Bolina::getDataCriacao);
    }
}
