package com.nt.recipe.mapper;


import com.nt.recipe.domain.po.RecipeFoodPO;
import com.nt.recipe.domain.po.RecipePO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RecipeMapper {
    int insertRecipe(RecipePO recipe);

    void insertRecipeFoods(List<RecipeFoodPO> foods);
}
