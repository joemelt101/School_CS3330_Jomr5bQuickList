/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.view;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.util.Callback;
import jomr5bquicklist.model.CookBook;
import jomr5bquicklist.model.IShoppingEntry;

/**
 *
 * @author Jared
 */
public class IShoppingEntryReadOnlyCellFactory implements Callback<ListView<IShoppingEntry>, ListCell<IShoppingEntry>>
{
    @Override
    public ListCell<IShoppingEntry> call(ListView<IShoppingEntry> param)
    {
        final ListCell<IShoppingEntry> cell = new ListCell<IShoppingEntry>()
        {
            {
                super.setPrefWidth(60);
            }
            
            @Override
            public void updateItem(IShoppingEntry item, boolean empty)
            {
                super.updateItem(item, empty);
                
                if (item != null)
                {
                    this.setText(item.getName());
                    
                    //check if it is a Recipe or an Ingredient and adjust accordingly...
                    if (CookBook.isRecipe(item.getName()))
                    {
                        //make this bold if it is a Recipe
                        this.setStyle("-fx-font-weight: bold");
                    }
                }
                else
                {
                    this.setText("");
                    this.setStyle("-fx-font-weight: normal");
                }
            }
        };
        
        return cell;   
    }
}
