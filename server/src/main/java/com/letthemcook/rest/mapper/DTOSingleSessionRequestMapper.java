package com.letthemcook.rest.mapper;

import com.letthemcook.sessionrequest.QueueStatus;
import com.letthemcook.sessionrequest.SessionRequest;
import com.letthemcook.sessionrequest.SingleSessionRequests;
import com.letthemcook.sessionrequest.dto.SessionRequestGetSingleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface DTOSingleSessionRequestMapper {

  DTOSingleSessionRequestMapper INSTANCE = Mappers.getMapper(DTOSingleSessionRequestMapper.class);

  // ######################################### Accept/Deny session request #########################################

  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "queueStatus", source = "queueStatus")
  SessionRequestGetSingleDTO convertEntityToGetSingleSessionRequestsDTO(SingleSessionRequests singleSessionRequests);
}
