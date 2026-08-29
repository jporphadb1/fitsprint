package com.jporpha.fitsprint.entity;

/**
 * Tipo da Bolina. Cada valor mapeia obrigatoriamente para uma {@link ZonaOperativa}
 * (regra de negócio: 100% do enum deve estar coberto por uma zona).
 */
public enum TaskType {
    URGENCIA(ZonaOperativa.CONTINUIDAD_OPERATIVA),
    BUG_NUEVO(ZonaOperativa.CONTINUIDAD_OPERATIVA),
    BUG_PLANEADO(ZonaOperativa.SPRINT_NORMAL),
    REQ_PLANEADO(ZonaOperativa.SPRINT_NORMAL);

    private final ZonaOperativa zona;

    TaskType(ZonaOperativa zona) {
        this.zona = zona;
    }

    public ZonaOperativa getZona() {
        return zona;
    }
}
