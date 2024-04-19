package com.letthemcook.rest.mapper;

import com.letthemcook.auth.token.Token;
import com.letthemcook.auth.token.dto.TokenResponseDTO;
import com.letthemcook.user.dto.GetMeRequestDTO;
import com.letthemcook.user.dto.LoginRequestDTO;
import com.letthemcook.user.dto.LogoutRequestDTO;
import com.letthemcook.user.dto.RegisterRequestDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.letthemcook.user.User;

@Mapper
public interface DTOUserMapper {

  DTOUserMapper INSTANCE = Mappers.getMapper(DTOUserMapper.class);

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
  @Mapping(source = "firstname", target = "firstname")
  @Mapping(source = "lastname", target = "lastname")
  @Mapping(source = "password", target = "password")
  User convertRegisterDTOtoEntity(RegisterRequestDTO registerRequestDTO);

  // ######################################### GET me #########################################

  @Mapping(source = "email", target = "email")
  @Mapping(source = "firstname", target = "firstname")
  @Mapping(source = "lastname", target = "lastname")
  @Mapping(source = "username", target = "username")
  GetMeRequestDTO convertEntityToGetMeResponseDTO(User user);

}
