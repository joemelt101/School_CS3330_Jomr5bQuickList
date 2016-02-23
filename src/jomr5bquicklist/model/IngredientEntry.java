/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.model;

import java.util.ArrayList;

public class IngredientEntry implements IShoppingEntry
{
    private String unit;
    private Float amount = 1f;
    private final Ingredient ingredient;
    
    public IngredientEntry(Ingredient ingredient, String unit, Float amount)
    {
        this.unit = unit;
        this.amount = amount;
        this.ingredient = ingredient;
    }
    
    @Override
    public String getName()
    {
        return this.ingredient.getName();
    }
    
    @Override
    public String getUnit()
    {
        return this.unit;
    }
    
    @Override
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    
    @Override
    public Float getAmount()
    {
        return this.amount;
    }
    
    @Override
    public void setAmount(Float amount)
    {
        this.amount = amount;
    }
    
    @Override
    public ArrayList<IngredientEntry> getIngredientEntries()
    {
        ArrayList<IngredientEntry> entries = new ArrayList<>();
        entries.add(this);
        return entries;
    }
}
