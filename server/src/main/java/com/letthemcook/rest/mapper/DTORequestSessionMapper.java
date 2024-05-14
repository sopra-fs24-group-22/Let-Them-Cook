package com.letthemcook.rest.mapper;

import com.letthemcook.sessionrequest.SessionRequest;
import com.letthemcook.sessionrequest.dto.SessionRequestDTO;
import com.letthemcook.sessionrequest.dto.SessionRequestGetSingleDTO;
import com.letthemcook.sessionrequest.dto.SessionRequestsGetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTORequestSessionMapper {

  DTORequestSessionMapper INSTANCE = Mappers.getMapper(DTORequestSessionMapper.class);

  // ######################################### Accept/Deny session request #########################################

  @Mapping(target = "userId", source = "userId")
  SessionRequest convertSessionRequestDTOToEntity(SessionRequestDTO sessionRequestDTO);

  // ######################################### Get user session requests #########################################
  @Mapping(target = "userSessions", source = "userSessions")
  SessionRequestsGetDTO convertEntityToGetSessionRequestsDTO(SessionRequest sessionRequest);
}
