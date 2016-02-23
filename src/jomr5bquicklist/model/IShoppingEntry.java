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
public interface IShoppingEntry
{
    String getName();

    Float getAmount();

    void setAmount(Float amount);

    ArrayList<IngredientEntry> getIngredientEntries();

    String getUnit();
    
    void setUnit(String str);
}
