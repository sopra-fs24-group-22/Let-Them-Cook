package com.letthemcook.rest.mapper;

import com.letthemcook.session.Session;
import com.letthemcook.session.dto.SessionCredentialsDTO;
import com.letthemcook.session.dto.SessionDTO;
import com.letthemcook.session.dto.SessionPostDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOSessionMapper {

  DTOSessionMapper INSTANCE = Mappers.getMapper(DTOSessionMapper.class);

  // ######################################### POST session #########################################

  @Mapping(source = "sessionName", target = "sessionName")
  @Mapping(source = "recipe", target = "recipeId")
  @Mapping(source = "maxParticipantCount", target = "maxParticipantCount")
  @Mapping(source = "date", target = "date")
  Session convertSingleSessionDTOToEntity(SessionPostDTO sessionPostDTO);

// ######################################### GET single session #########################################

  @Mapping(source = "sessionName", target = "sessionName")
  @Mapping(source = "hostId", target = "host")
  @Mapping(source = "recipeId", target = "recipe")
  @Mapping(source = "maxParticipantCount", target = "maxParticipantCount")
  @Mapping(source = "date", target = "date")
  SessionDTO convertEntityToSingleSessionDTO(Session session);

  // ######################################### GET session credentials #########################################

  @Mapping(source = "hostId", target = "hostId")
  @Mapping(source = "roomId", target = "roomId")
  SessionCredentialsDTO convertEntityToSessionCredentialsDTO(Session session);
}
