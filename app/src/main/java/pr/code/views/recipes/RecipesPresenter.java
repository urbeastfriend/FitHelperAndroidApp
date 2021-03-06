package pr.code.views.recipes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pr.code.models.Categories;
import pr.code.models.Meals;
import pr.code.utils.DBHelper;

/**
 * This presenter class is used to retrieve necessary data from database and send it to the fragment
 * within this presenter was called
 */
public class RecipesPresenter {

    private RecipesView view;


    public RecipesPresenter(RecipesView view) {
        this.view = view;

    }

    void getRecipes(SQLiteDatabase database) {
        view.showLoading();

        try {
            List<Meals.Meal> templist = loadRecipes(database);
            Collections.shuffle(templist);
            view.setMeal(templist);
        }
        catch(Exception ex){
            view.onErrorLoading("При получении данных произошла ошибка" + ex.getMessage());
        }
        finally {
            view.hideLoading();
        }

    }

    void getCategories(SQLiteDatabase database) {
        view.showLoading();

        try {
            view.setCategory(loadCategories(database));
        }
        catch(Exception ex){
            view.onErrorLoading(ex.getMessage());
        }
        finally {
            view.hideLoading();
        }

    }

    List<Meals.Meal> loadRecipes(SQLiteDatabase database) {
        List<Meals.Meal> res = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * from " + DBHelper.TABLE_RECIPES, null);

        if (cursor.moveToFirst()) {
            int idMeal = cursor.getColumnIndex(DBHelper.KEY_IDRECIPE);
            int strMeal = cursor.getColumnIndex(DBHelper.KEY_NAMERECIPE);
            int strCategory = cursor.getColumnIndex(DBHelper.KEY_CATEGORYRECIPE);
            int strArea = cursor.getColumnIndex(DBHelper.KEY_AREARECIPE);
            int strInstructions = cursor.getColumnIndex(DBHelper.KEY_INSTRUCTIONSRECIPE);
            int MealThumb = cursor.getColumnIndex(DBHelper.KEY_PHOTORECIPE);
            int strTags = cursor.getColumnIndex(DBHelper.KEY_TAGSRECIPE);
            int ingredients = cursor.getColumnIndex(DBHelper.KEY_INGREDIENTSRECIPE);
            int measures = cursor.getColumnIndex(DBHelper.KEY_MEASURESRECIPE);
            int mealInfo = cursor.getColumnIndex(DBHelper.KEY_MEALINFO);
            int cooktime = cursor.getColumnIndex(DBHelper.KEY_COOKTIME);

            do {
                Meals.Meal tempRecipe = new Meals.Meal();


                tempRecipe.setIdMeal(cursor.getString(idMeal));
                tempRecipe.setStrMeal(cursor.getString(strMeal));
                tempRecipe.setStrCategory(cursor.getString(strCategory));
                tempRecipe.setStrArea(cursor.getString(strArea));
                tempRecipe.setStrInstructions(cursor.getString(strInstructions));
                tempRecipe.setStrMealThumb(cursor.getString(MealThumb));
                tempRecipe.setStrTags(cursor.getString(strTags));
                tempRecipe.setStrMealInfo(cursor.getString(mealInfo));
                tempRecipe.setStrCookTime(cursor.getString(cooktime));
                tempRecipe.setStrIngredients(cursor.getString(ingredients));
                tempRecipe.setStrMeasures(cursor.getString(measures));
                res.add(tempRecipe);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }


    List<Categories.Category> loadCategories(SQLiteDatabase database) {
        List<Categories.Category> res = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * from " + DBHelper.TABLE_CATEGORIES, null);

        if (cursor.moveToFirst()) {
            int idCategory = cursor.getColumnIndex(DBHelper.KEY_IDCATEGORY);
            int strCategory = cursor.getColumnIndex(DBHelper.KEY_NAMECATEGORY);
            int strCategoryThumb = cursor.getColumnIndex(DBHelper.KEY_PHOTOCATEGORY);
            int strCategoryDescription = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTIONCATEGORY);

            do {


                Categories.Category tempCategory = new Categories.Category();
                tempCategory.setIdCategory(cursor.getString(idCategory));
                tempCategory.setStrCategory(cursor.getString(strCategory));
                tempCategory.setStrCategoryThumb(cursor.getString(strCategoryThumb));
                tempCategory.setStrCategoryDescription(cursor.getString(strCategoryDescription));


                res.add(tempCategory);

            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }



}
