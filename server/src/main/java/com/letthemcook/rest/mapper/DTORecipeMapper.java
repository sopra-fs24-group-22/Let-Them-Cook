package com.letthemcook.rest.mapper;

import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.dto.RecipeGetDTO;
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
  @Mapping(source = "ingredients", target = "ingredients")
  @Mapping(source = "cookingTimeMin", target = "cookingTimeMin")
  Recipe convertRecipePostDTOToRecipe(RecipePostDTO recipePostDTO);

  // ######################################### GET recipe #########################################

  @Mapping(source = "id", target = "id")
  @Mapping(source = "creatorId", target = "creatorId")
  @Mapping(source = "creatorName", target = "creatorName")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "checklist", target = "checklist")
  @Mapping(source = "ingredients", target = "ingredients")
  @Mapping(source = "cookingTimeMin", target = "cookingTimeMin")
  RecipeGetDTO convertRecipeToRecipeGetDTO(Recipe recipe);

  // ######################################### GET recipe #########################################

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "checklist", target = "checklist")
  @Mapping(source = "ingredients", target = "ingredients")
  @Mapping(source = "cookingTimeMin", target = "cookingTimeMin")
  Recipe convertRecipePutDTOToEntity(RecipeGetDTO recipeGetDTO);
}
