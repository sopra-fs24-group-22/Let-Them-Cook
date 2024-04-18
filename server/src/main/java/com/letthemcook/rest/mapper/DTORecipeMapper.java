package com.letthemcook.rest.mapper;

import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.dto.RecipePostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTORecipeMapper {

  DTORecipeMapper INSTANCE = Mappers.getMapper(DTORecipeMapper.class);

  // ######################################### POST recipe #########################################

  @Mapping(source = "title", target = "title")
  @Mapping(source = "checklist", target = "checklist")
  @Mapping(source = "privacyStatus", target = "privacyStatus")
  Recipe convertRecipePostDTOToRecipe(RecipePostDTO recipePostDTO);

  @Mapping(source = "creatorId", target = "creatorId")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "checklist", target = "checklist")
  RecipePostDTO convertRecipeToRecipePostDTO(Recipe recipe);
}
