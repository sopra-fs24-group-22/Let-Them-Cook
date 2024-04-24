package com.letthemcook.rest.mapper;

import com.letthemcook.session.ChecklistStep;
import com.letthemcook.session.dto.CheckPutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOChecklistMapper {
    DTOChecklistMapper INSTANCE = Mappers.getMapper(DTOChecklistMapper.class);

    @Mapping(source = "stepIndex", target = "stepIndex")
    @Mapping(source = "isChecked", target = "isChecked")
    ChecklistStep convertCheckPutDTOToEntity(CheckPutDTO checkPutDTO);
}
