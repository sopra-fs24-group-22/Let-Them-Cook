package com.letthemcook.rest.mapper;

import com.letthemcook.rating.Rating;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.dto.RecipeGetDTO;
import com.letthemcook.recipe.dto.RecipePostDTO;
import com.letthemcook.recipe.dto.RecipeRatingGetDTO;
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

  @Mapping(source = "recipe.id", target = "id")
  @Mapping(source = "recipe.creatorId", target = "creatorId")
  @Mapping(source = "recipe.creatorName", target = "creatorName")
  @Mapping(source = "recipe.title", target = "title")
  @Mapping(source = "recipe.checklist", target = "checklist")
  @Mapping(source = "recipe.ingredients", target = "ingredients")
  @Mapping(source = "recipe.cookingTimeMin", target = "cookingTimeMin")
  @Mapping(source = "rating.avgTotalRating", target = "avgTotalRating")
  @Mapping(source = "rating.nrRatings", target = "nrRatings")
  RecipeRatingGetDTO convertRecipeAndRatingToRecipeRatingGetDTO(Recipe recipe, Rating rating);

  // ######################################### PUT recipe #########################################

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "checklist", target = "checklist")
  @Mapping(source = "ingredients", target = "ingredients")
  @Mapping(source = "cookingTimeMin", target = "cookingTimeMin")
  Recipe convertRecipePutDTOToEntity(RecipeGetDTO recipeGetDTO);
}
