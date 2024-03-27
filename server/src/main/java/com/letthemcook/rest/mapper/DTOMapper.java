package com.letthemcook.rest.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.letthemcook.User.User;
import com.letthemcook.User.UserGetDTO;

@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "id", target = "id")
  UserGetDTO convertEntityToUserGetDTO(User user);

}
