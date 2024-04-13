package com.letthemcook.rest.mapper;

import com.letthemcook.auth.token.Token;
import com.letthemcook.auth.token.dto.TokenResponseDTO;
import com.letthemcook.user.dto.LoginRequestDTO;
import com.letthemcook.user.dto.LogoutRequestDTO;
import com.letthemcook.user.dto.RegisterRequestDTO;
import com.letthemcook.user.dto.UserDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.letthemcook.user.User;

@Mapper
public interface DTOUserMapper {

  DTOUserMapper INSTANCE = Mappers.getMapper(DTOUserMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "password", target = "password")
  UserDTO convertEntityToUserGetDTO(User user);

  // ######################################### Login #########################################

  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserLoginDTOToEntity(LoginRequestDTO loginRequestDTO);

  // ######################################### Logout #########################################

  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  User convertUserLogoutDTOToEntity(LogoutRequestDTO logoutRequestDTO);

  // ######################################### Token #########################################

  @Mapping(source = "accessToken", target = "accessToken")
  @Mapping(source = "refreshToken", target = "refreshToken")
  TokenResponseDTO convertEntityToTokenDTO(Token token);

  // ######################################### Register #########################################

  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "password", target = "password")
  User convertRegisterDTOtoEntity(RegisterRequestDTO registerRequestDTO);

}
