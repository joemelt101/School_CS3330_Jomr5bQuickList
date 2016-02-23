/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.model;

import java.util.ArrayList;

/**
 *
 * @author Jared
 */
public class RecipeEntry implements IShoppingEntry
{
    private Recipe recipe;
    private Float desiredServings;
    
    public RecipeEntry(Recipe recipe)
    {
        this.recipe = recipe;
        this.desiredServings = recipe.getServingSize();
    }

    @Override
    public String getName()
    {
        return this.recipe.getName();
    }
    
    @Override
    public Float getAmount()
    {
        return this.desiredServings;
    }
    
    @Override
    public void setAmount(Float amount)
    {
        this.desiredServings = amount;
    }
    
    @Override
    public ArrayList<IngredientEntry> getIngredientEntries()
    {
        //return IngredientEntries with size adjustments made
        Float ratio = this.desiredServings / this.recipe.getServingSize();
        
        ArrayList<IngredientEntry> adjList = new ArrayList<>();
        
        //now adjust the amounts in each IngredientEntry and return accordingly...
        for (IngredientEntry entry : this.recipe.getIngredients().values())
        {
            adjList.add(new IngredientEntry(CookBook.getIngredient(entry.getName()), entry.getUnit(), entry.getAmount() * ratio));
        }
        
        //return the adjusted list...
        return adjList;
    }
    
    @Override
    public String getUnit()
    {
        return "Servings";
    }
    
    @Override
    public void setUnit(String str)
    {
        System.out.println("Setting the unit for Recipes is NOT accepted.");
    }
}
