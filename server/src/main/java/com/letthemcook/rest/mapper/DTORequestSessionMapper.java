package com.letthemcook.rest.mapper;

import com.letthemcook.sessionrequest.SessionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTORequestSessionMapper {

  DTORequestSessionMapper INSTANCE = Mappers.getMapper(DTORequestSessionMapper.class);

  // ######################################### SOME session request #########################################
}
