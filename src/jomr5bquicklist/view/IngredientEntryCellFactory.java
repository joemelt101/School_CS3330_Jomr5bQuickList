/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.view;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jomr5bquicklist.model.IngredientEntry;

/**
 *
 * @author Jared
 */
public class IngredientEntryCellFactory implements Callback<ListView<IngredientEntry>, ListCell<IngredientEntry>>
{
    @Override
    public ListCell<IngredientEntry> call(ListView<IngredientEntry> param)
    {
        final ListCell<IngredientEntry> cell = new ListCell<IngredientEntry>()
        {
            {
                super.setPrefWidth(100);
            }
            
            @Override
            public void updateItem(IngredientEntry item, boolean empty)
            {
                super.updateItem(item, empty);
                
                if (item != null)
                {
                    this.setText(item.getName() + " (" + item.getAmount() + " " + item.getUnit() + ")");
                }
            }
        };
        
        return cell;        
    }
}
