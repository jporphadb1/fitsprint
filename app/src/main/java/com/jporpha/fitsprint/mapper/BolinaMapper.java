package com.jporpha.fitsprint.mapper;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.entity.Bolina;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BolinaMapper {

    @Mapping(target = "ratio", expression = "java(bolina.getRatio())")
    @Mapping(target = "zona", expression = "java(bolina.getZona())")
    @Mapping(target = "developerId", source = "developer.id")
    @Mapping(target = "developerNome", source = "developer.nome")
    BolinaResponse toResponse(Bolina bolina);
}
