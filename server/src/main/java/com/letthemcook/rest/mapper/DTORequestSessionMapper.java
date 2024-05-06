package com.letthemcook.rest.mapper;

import com.letthemcook.sessionrequest.SessionRequest;
import com.letthemcook.sessionrequest.dto.SessionRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTORequestSessionMapper {

  DTORequestSessionMapper INSTANCE = Mappers.getMapper(DTORequestSessionMapper.class);

  // ######################################### Accept/Deny session request #########################################

  @Mapping(target = "userId", source = "userId")
  SessionRequest convertSessionRequestDTOToEntity(SessionRequestDTO sessionRequestDTO);
}
