package com.letthemcook.app.rest.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.letthemcook.app.entity.User;
import com.letthemcook.app.rest.dto.UserGetDTO;
import com.letthemcook.app.rest.dto.UserGetSelfDTO;
import com.letthemcook.app.rest.dto.UserPostDTO;
import com.letthemcook.app.rest.dto.UserPutDTO;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "username", target = "username")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "creationDate", target = "creationDate")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "token", target = "token")
  UserGetSelfDTO convertEntityToUserGetSelfDTO(User user);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "birthday", target = "birthday")
  User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);
}
