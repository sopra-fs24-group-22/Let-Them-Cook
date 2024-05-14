package com.letthemcook.rest.mapper;

import com.letthemcook.auth.token.Token;
import com.letthemcook.auth.token.dto.TokenResponseDTO;
import com.letthemcook.rating.Rating;
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

  // ######################################### Register and Update #########################################

  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "firstname", target = "firstname")
  @Mapping(source = "lastname", target = "lastname")
  @Mapping(source = "password", target = "password")
  User convertRegisterDTOtoEntity(RegisterRequestDTO registerRequestDTO);

  // ######################################### GET User #########################################

  @Mapping(source = "user.email", target = "email")
  @Mapping(source = "user.firstname", target = "firstname")
  @Mapping(source = "user.lastname", target = "lastname")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "user.id", target = "id")
  @Mapping(source = "rating.avgTotalRating", target = "avgTotalRating")
  @Mapping(source = "rating.nrRatings", target = "nrRatings")
  GetMeRequestDTO convertEntityToGetMeResponseDTO(User user, Rating rating);

}
