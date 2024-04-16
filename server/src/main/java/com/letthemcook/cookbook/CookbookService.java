package com.letthemcook.cookbook;

import com.letthemcook.util.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CookbookService {
  private final CookbookRepository cookbookRepository;
  private final SequenceGeneratorService sequenceGeneratorService;

  @Autowired
  public CookbookService(CookbookRepository cookbookRepository, SequenceGeneratorService sequenceGeneratorService) {
    this.cookbookRepository = cookbookRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
  }

  public Cookbook createCookbook(Long ownerId) {
    Cookbook cookbook = new Cookbook(ownerId);
    cookbook.setId(sequenceGeneratorService.getSequenceNumber(Cookbook.SEQUENCE_NAME));
    cookbookRepository.save(cookbook);

    return cookbook;
  }

  public void addRecipeToCookbook(Long ownerId, Long recipeId) {
    Cookbook cookbook = cookbookRepository.getByOwnerId(ownerId);
    cookbook.addRecipe(recipeId);
    cookbookRepository.save(cookbook);
  }
}
