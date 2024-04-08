package com.letthemcook.rest.mapper;

import com.letthemcook.user.UserDTO;
import com.letthemcook.auth.refreshToken.TokenDTO;
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

  // ######################################### Login #########################################

  @Mapping(source = "email", target = "email")
  @Mapping(source = "password", target = "password")
  User convertUserLoginDTOToEntity(UserDTO userDTO);

  @Mapping(source = "accessToken", target = "accessToken")
  TokenDTO convertEntityToTokenDTO(User user);

  // ######################################### Refresh Token #########################################

  @Mapping(source = "refreshToken", target = "refreshToken")
  User convertTokenDTOToRefresh(TokenDTO tokenDTO);

  @Mapping(source = "refreshToken", target = "refreshToken")
  TokenDTO convertRefreshToTokenDTO(User user);

  // ######################################### Register #########################################

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
