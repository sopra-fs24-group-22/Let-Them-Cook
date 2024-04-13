package com.letthemcook.rest.mapper;

import com.letthemcook.session.Session;
import com.letthemcook.session.dto.SessionDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

public interface DTOSessionMapper {

  DTOSessionMapper INSTANCE = Mappers.getMapper(DTOSessionMapper.class);

  // ######################################### POST session #########################################

  @Mapping(source = "sessionName", target = "sessionName")
  Session convertSessionDTOToEntity(SessionDTO sessionDTO);



}
