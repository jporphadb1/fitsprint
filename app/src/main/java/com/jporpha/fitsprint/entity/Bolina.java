package com.jporpha.fitsprint.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tarefa do sprint ("bolina" no glossário do domínio).
 */
@Entity
@Table(name = "bolina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bolina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    /** Esforço em escala Fibonacci (1,2,3,5,8,13,21). */
    @Column(nullable = false)
    private Integer tamanho;

    /** Valor de negócio, número livre, usado no ratio = valor / tamanho. */
    @Column(nullable = false)
    private Integer valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType tipo;

    /** Classificação manual de importância (PO/SM). Pode não ter sido definida ainda. */
    @Enumerated(EnumType.STRING)
    private Importancia importancia;

    /** Prioridade final manual, definida pelo PO; sobrepõe o ratio na ordenação. */
    @Column(name = "prioridade_final")
    private Integer prioridadeFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus estado = TaskStatus.TODO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    /**
     * Denormalizado a partir de sprint.team, para permitir filtragem direta por
     * team_id (extraído do JWT) em todas as queries, sem join obrigatório.
     */
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean fuera = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean eliminado = false;

    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    /** ratio = valor / tamanho, nunca persistido. */
    @Transient
    public double getRatio() {
        if (tamanho == null || tamanho == 0) {
            return 0d;
        }
        return valor == null ? 0d : (double) valor / tamanho;
    }

    @Transient
    public ZonaOperativa getZona() {
        return tipo.getZona();
    }
}
