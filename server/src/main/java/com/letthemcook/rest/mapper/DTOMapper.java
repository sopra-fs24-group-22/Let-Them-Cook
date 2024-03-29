package com.letthemcook.rest.mapper;

import com.letthemcook.user.UserDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.letthemcook.user.User;

@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "password", target = "password")
  UserDTO convertEntityToUserGetDTO(User user);

// ################## Login ########################################
  @Mapping(source = "email", target = "email")
  @Mapping(source = "password", target = "password")
  User convertUserLoginDTOToEntity(UserDTO userDTO);

  @Mapping(source = "token", target = "token")
  UserDTO convertEntityToUserLoginDTO(User user);

// ################## Register #####################################
  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "password", target = "password")
  User convertUserPostDTOToEntity(UserDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "password", target = "password")
  UserDTO convertEntityToUserPostDTO(User user);
}
