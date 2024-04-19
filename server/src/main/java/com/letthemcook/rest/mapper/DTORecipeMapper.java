package com.letthemcook.rest.mapper;

import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.dto.RecipeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTORecipeMapper {

  DTORecipeMapper INSTANCE = Mappers.getMapper(DTORecipeMapper.class);

  // ######################################### POST / GET recipe #########################################

  @Mapping(source = "title", target = "title")
  @Mapping(source = "checklist", target = "checklist")
  @Mapping(source = "ingredients", target = "ingredients")
  @Mapping(source = "cookingTimeMin", target = "cookingTimeMin")
  Recipe convertRecipePostDTOToRecipe(RecipeDTO recipeDTO);

  @Mapping(source = "creatorId", target = "creatorId")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "checklist", target = "checklist")
  @Mapping(source = "ingredients", target = "ingredients")
  @Mapping(source = "cookingTimeMin", target = "cookingTimeMin")
  RecipeDTO convertRecipeToRecipePostDTO(Recipe recipe);
}
