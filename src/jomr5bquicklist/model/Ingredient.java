/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.model;

/**
 *
 * @author Jared
 */
public class Ingredient
{
    //Make this accessible to the CookBook
    private String name;
    
    public Ingredient(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String newName)
    {
        this.name = newName;
        
        CookBook.changeIngredientName(this.name, newName);
    }
}
