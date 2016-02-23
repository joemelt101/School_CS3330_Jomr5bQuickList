/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.model;

import java.util.HashMap;

/**
 *
 * @author Jared
 */
public class Recipe
{
    //make this accessible to the CookBook
    private String name;
    private final HashMap<String, IngredientEntry> ingredients = new HashMap<>();
    private Float servingSize;
    
    public Recipe(String name)
    {
        this.name = name;
    }
    
    public Recipe(String name, Float servingSize)
    {
        this(name);
        this.servingSize = servingSize;
    }
    
    
    public String getName()
    {
        return this.name;
    }
    
    public void setName(String newName)
    {
        //MUST adjust through CookBook for proper name registration...
        CookBook.changeRecipeName(this.name, newName);
    }
    
    public Float getServingSize()
    {
        return this.servingSize;
    }
    
    public void setServingSize(Float servingSize)
    {
        this.servingSize = servingSize;
    }
    
    public HashMap<String, IngredientEntry> getIngredients()
    {
        return this.ingredients;
    }
    
    public void addIngredient(String name, Float amount, String unit)
    {
        Ingredient ing = CookBook.getIngredient(name);
        
        if (ing == null)
        {
            //hasn't been created yet...
            CookBook.addIngredient(name);
            ing = CookBook.getIngredient(name);
        }
        
        IngredientEntry entry = new IngredientEntry(ing, unit, amount);
        
        //add the ingredient to the list of ingredients...
        this.ingredients.put(entry.getName(), entry);
    }
    
    public void editIngredient(String name, Float amount)
    {
        this.ingredients.get(name).setAmount(amount);
    }
    
    public void removeIngredient(String name)
    {
        this.ingredients.remove(name);
    }
}
    
    
