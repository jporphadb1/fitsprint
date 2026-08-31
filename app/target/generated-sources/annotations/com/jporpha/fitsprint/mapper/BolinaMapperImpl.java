package com.jporpha.fitsprint.mapper;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.entity.Bolina;
import com.jporpha.fitsprint.entity.Developer;
import com.jporpha.fitsprint.entity.Importancia;
import com.jporpha.fitsprint.entity.TaskStatus;
import com.jporpha.fitsprint.entity.TaskType;
import com.jporpha.fitsprint.entity.ZonaOperativa;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-29T18:55:49-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.20 (Eclipse Adoptium)"
)
@Component
public class BolinaMapperImpl implements BolinaMapper {

    @Override
    public BolinaResponse toResponse(Bolina bolina) {
        if ( bolina == null ) {
            return null;
        }

        Long developerId = null;
        String developerNome = null;
        Long id = null;
        String titulo = null;
        Integer tamanho = null;
        Integer valor = null;
        TaskType tipo = null;
        Importancia importancia = null;
        Integer prioridadeFinal = null;
        TaskStatus estado = null;
        Boolean fuera = null;
        LocalDateTime dataCriacao = null;

        developerId = bolinaDeveloperId( bolina );
        developerNome = bolinaDeveloperNome( bolina );
        id = bolina.getId();
        titulo = bolina.getTitulo();
        tamanho = bolina.getTamanho();
        valor = bolina.getValor();
        tipo = bolina.getTipo();
        importancia = bolina.getImportancia();
        prioridadeFinal = bolina.getPrioridadeFinal();
        estado = bolina.getEstado();
        fuera = bolina.getFuera();
        dataCriacao = bolina.getDataCriacao();

        double ratio = bolina.getRatio();
        ZonaOperativa zona = bolina.getZona();

        BolinaResponse bolinaResponse = new BolinaResponse( id, titulo, tamanho, valor, ratio, tipo, zona, importancia, prioridadeFinal, estado, developerId, developerNome, fuera, dataCriacao );

        return bolinaResponse;
    }

    private Long bolinaDeveloperId(Bolina bolina) {
        if ( bolina == null ) {
            return null;
        }
        Developer developer = bolina.getDeveloper();
        if ( developer == null ) {
            return null;
        }
        Long id = developer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bolinaDeveloperNome(Bolina bolina) {
        if ( bolina == null ) {
            return null;
        }
        Developer developer = bolina.getDeveloper();
        if ( developer == null ) {
            return null;
        }
        String nome = developer.getNome();
        if ( nome == null ) {
            return null;
        }
        return nome;
    }
}
