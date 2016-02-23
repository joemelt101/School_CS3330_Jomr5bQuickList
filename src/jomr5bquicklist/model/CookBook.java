/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jared
 */
public class CookBook extends StaticSaveable
{
    private static final HashMap<String, Ingredient> createdIngredients = new HashMap<>();
    private static final HashMap<String, Recipe> createdRecipes = new HashMap<>();
    
    private static Boolean isUnusedKey(String key)
    {
        return !(createdIngredients.containsKey(key) == true || createdRecipes.containsKey(key) == true);
    }
    
    public static Boolean isRecipe(String key)
    {
        return createdRecipes.containsKey(key);
    }
    
    public static Boolean isIngredient(String key)
    {
        return createdIngredients.containsKey(key);
    }
    
    /**
     * Gets the currently added ingredients.
     * @return A HashMap<String, Ingredient> of the currently created ingredients.
     */
    public static HashMap<String, Ingredient> getIngredients()
    {
        return createdIngredients;
    }
    
    /**
     * Gets the Ingredient with the given name.
     * @param name The name of the Ingredient to get.
     * @return The Ingredient with the given name.
     */
    public static Ingredient getIngredient(String name)
    {
        return createdIngredients.get(name);
    }
    
    /**
     * Adds an ingredient with the given name to the CookBook.
     * @param name The name of the ingredient to add to the CookBook.
     */
    public static void addIngredient(String name)
    {
        //if the name has not been used yet...
        if (isUnusedKey(name) == true)
        {
            //go ahead and use the name to create the ingredient
            createdIngredients.put(name, new Ingredient(name));
        }
    }
    
    /**
     * Removes an Ingredient from the CookBook
     * @param nameToDelete The name of the Ingredient to remove.
     * @return 
     */
    public static List<Recipe> removeIngredient(String nameToDelete)
    {
        if (createdIngredients.containsKey(nameToDelete) == true)
        {
            ArrayList<Recipe> cUsing = new ArrayList<>();
            
            //now that we know the ingredient exists, check to see if it is in use by any recipes
            ArrayList<Recipe> rList = new ArrayList<>();
            rList.addAll(createdRecipes.values());
            
            for (int i = 0; i < rList.size(); ++i)
            {
                ArrayList<IngredientEntry> iList = new ArrayList<>();
                
                iList.addAll(rList.get(i).getIngredients().values());
                
                for (int j = 0; j < iList.size(); ++j)
                {
                    //if an ingredient is equivalent to the one being removed...
                    if (iList.get(j).getName().equals(nameToDelete))
                    {
                        //can't remove the ingredient!
                        cUsing.add(rList.get(i));
                        break; //no point in searching further in the current recipe
                    }
                }
            }
            
            if (cUsing.isEmpty())
            {
                //didn't find any recipes using this ingredient so delete!
                createdIngredients.remove(nameToDelete);
                return null;
            }
            else
            {
                return cUsing; //notify what Recipes are currently using the ingredient
            }
        }
        
        //Ingredient doesn't exist anyways so ignore...
        return null;
    }
    
    /**
     * Gets the Recipe with the given name.
     * @param name The name of the Recipe to get.
     * @return The Recipe with the given name.
     */
    public static Recipe getRecipe(String name)
    {
        return createdRecipes.get(name);
    }
    
    /**
     * Gets all of the Recipes in a HashMap<String, Recipe> format.
     * @return All of the currently added Recipes.
     */
    public static HashMap<String, Recipe> getRecipes()
    {
        return createdRecipes;
    }
    
    public static ArrayList<IShoppingEntry> getAllEntries()
    {
        ArrayList<IShoppingEntry> list = new ArrayList<>();
        
        //add ingredients
        Iterator<Ingredient> i = createdIngredients.values().iterator();
        
        while (i.hasNext())
        {
            Ingredient ing = i.next();
            
            //create an IngredientEntry
            IngredientEntry entry = new IngredientEntry(ing, "", 0f);
            
            //add to the list
            list.add(entry);
        }
        
        //add Recipes
        Iterator<Recipe> r = createdRecipes.values().iterator();
        
        while (r.hasNext())
        {
            Recipe rec = r.next();
            
            //create a RecipeEntry
            RecipeEntry entry = new RecipeEntry(rec);
            
            //add to the list
            list.add(entry);
        }
        
        return list;
    }
    
    /**
     * Adds a Recipe to the CookBook. If a Recipe with the given name already exists, it will be replaced.
     * @param name The name of the Recipe to add to the CookBook.
     * @param servingSize The serving size of the Recipe.
     * @param ingredients The ingredients in the Recipe.
     */
    public static void addRecipe(String name, Float servingSize, List<IngredientEntry> ingredients)
    {
        Recipe rec = new Recipe(name, servingSize);
        
        if (ingredients != null)
        {
            for (IngredientEntry entry : ingredients)
            {
                rec.addIngredient(entry.getName(), entry.getAmount(), entry.getUnit());
            }
        }
        
        createdRecipes.put(name, rec);
    }
    
    public static void removeRecipe(String name)
    {
        createdRecipes.remove(name);
    }
    
    /**
     * Saves the CookBook to a text file in a format that can be loaded immediately with CookBook.LoadCookBook(String url).
     * @param url The URL of text file to save the CookBook to.
     */
    public static void saveCookBook(String url)
    {
        String data = "";
        
        for (Ingredient ing : createdIngredients.values())
        {
            data += ing.getName() + "\r\n";
        }
        
        data += "*\r\n"; //Mark the start of the recipes
        
        for (Recipe rec : createdRecipes.values())
        {
            data += rec.getName();
            data += "|" + rec.getServingSize();
            
            for (IngredientEntry ing : rec.getIngredients().values())
            {
                data += "|" + ing.getName() + "~" + ing.getAmount() + "~" + ing.getUnit();
            }
            
            data += "\r\n";
        }
        
        save(url, data);
    }
    
    /**
     * Loads a text file and parses Ingredients and Recipes from it.
     * @param url The URL of the file to load into the CookBook.
     */
    public static void loadCookBook(String url)
    {
        List<String> lines = load(url);
        
        Boolean addingIngredients = true;
        
        for (String line : lines)
        {
            if (addingIngredients == true)
            {
                if ("*".equals(line))
                {
                    //found where the ingredients end and the Recipes begin...
                    addingIngredients = false;
                    continue; //jump to the first recipe...
                }
                
                //else, still adding ingredients
                createdIngredients.put(line, new Ingredient(line));
            }
            else
            {
                //currently importing recipes
                String[] split = line.split("\\|"); //need to escape the pipe character for proper split
                //[0] = name, [1] = servingSize, [2] = Ingredients
                String name = split[0];
                Float servingSize = Float.parseFloat(split[1]);
                Recipe rec = new Recipe(name, servingSize);
                
                //import ingredients for recipe
                for (int i = 2; i < split.length; ++i)
                {
                    String[] splitIng = split[i].split("~");
                    rec.addIngredient(splitIng[0], Float.parseFloat(splitIng[1]), splitIng[2]);
                }
                
                //finally, add the parsed value to the CookBook...
                createdRecipes.put(rec.getName(), rec);
            }
        }
    }
    
    protected static void changeRecipeName(String oldName, String newName)
    {
        Recipe rec = getRecipe(oldName);
        
        //first update the Recipe's name locally
        rec.setName(newName);
        
        //now update within the CookBook
        createdRecipes.put(newName, createdRecipes.get(oldName));
        
        //now delete old reference
        createdRecipes.remove(oldName);
    }
    
    protected static void changeIngredientName(String oldName, String newName)
    {
        Ingredient ing = getIngredient(oldName);
        
        //first update the Recipe's name locally
        ing.setName(newName);
        
        //now update within the CookBook
        createdIngredients.put(newName, createdIngredients.get(oldName));
        
        //now delete old reference
        createdRecipes.remove(oldName);
    }
}
