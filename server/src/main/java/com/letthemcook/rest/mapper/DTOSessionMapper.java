package com.letthemcook.rest.mapper;

import com.letthemcook.session.Session;
import com.letthemcook.session.SessionUserState;
import com.letthemcook.session.dto.SessionCredentialsDTO;
import com.letthemcook.session.dto.SessionDTO;
import com.letthemcook.session.dto.SessionPostDTO;
import com.letthemcook.session.dto.SessionUserStateDTO;
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
  @Mapping(source = "duration", target = "duration")
  Session convertSingleSessionDTOToEntity(SessionPostDTO sessionPostDTO);

// ######################################### GET single session #########################################

  @Mapping(source = "sessionName", target = "sessionName")
  @Mapping(source = "hostId", target = "host")
  @Mapping(source = "hostName", target = "hostName")
  @Mapping(source = "recipeId", target = "recipe")
  @Mapping(source = "recipeName", target = "recipeName")
  @Mapping(source = "maxParticipantCount", target = "maxParticipantCount")
  @Mapping(source = "currentParticipantCount", target = "currentParticipantCount")
  @Mapping(source = "participants", target = "participants")
  @Mapping(source = "date", target = "date")
  @Mapping(source = "duration", target = "duration")
  SessionDTO convertEntityToSingleSessionDTO(Session session);

  // ######################################### GET session credentials #########################################

  @Mapping(source = "hostId", target = "hostId")
  @Mapping(source = "roomId", target = "roomId")
  @Mapping(source = "id", target = "sessionId")
  @Mapping(source = "recipeId", target = "recipeId")
  SessionCredentialsDTO convertEntityToSessionCredentialsDTO(Session session);

  // ######################################### SessionUserState #########################################

  @Mapping(source = "sessionId", target = "sessionId")
  @Mapping(source = "recipeSteps", target = "recipeSteps")
  @Mapping(source = "currentStepValues", target = "currentStepValues")
  SessionUserStateDTO convertEntityToSessionUserStateDTO(SessionUserState sessionUserState);

  // ######################################### PUT Session #########################################

  @Mapping(source = "sessionName", target = "sessionName")
  @Mapping(source = "host", target = "hostId")
  @Mapping(source = "hostName", target = "hostName")
  @Mapping(source = "recipe", target = "recipeId")
  @Mapping(source = "recipeName", target = "recipeName")
  @Mapping(source = "maxParticipantCount", target = "maxParticipantCount")
  @Mapping(source = "currentParticipantCount", target = "currentParticipantCount")
  @Mapping(source = "participants", target = "participants")
  @Mapping(source = "date", target = "date")
  @Mapping(source = "duration", target = "duration")
  Session convertEntityToSessionPutDTO(SessionDTO sessionDTO);
}
