package com.letthemcook.rest.mapper;

import com.letthemcook.sessionrequest.SingleSessionRequests;
import com.letthemcook.sessionrequest.dto.SessionRequestGetSingleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOSingleSessionRequestMapper {

  DTOSingleSessionRequestMapper INSTANCE = Mappers.getMapper(DTOSingleSessionRequestMapper.class);

  // ######################################### Accept/Deny session request #########################################

  @Mapping(target = "sessionRequests", source = "sessionRequests")
  SessionRequestGetSingleDTO convertEntityToGetSingleSessionRequestsDTO(SingleSessionRequests singleSessionRequests);
}
