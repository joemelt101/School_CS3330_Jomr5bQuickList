/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import jomr5bquicklist.controller.Helper;
import jomr5bquicklist.model.CookBook;
import jomr5bquicklist.model.IShoppingEntry;

/**
 *
 * @author Jared
 */
public class IShoppingEntryEditableCellFactory implements Callback<ListView<IShoppingEntry>, ListCell<IShoppingEntry>>
{
    @Override
    public ListCell<IShoppingEntry> call(ListView<IShoppingEntry> param)
    {
        final ListCell<IShoppingEntry> cell = new ListCell<IShoppingEntry>()
        {
            private final HBox hbox;
            private final TextField amount;
            private final TextField unit;
            private final Label text;
                        
            //Constructor...
            {
                super.setPrefWidth(100);
                //this.setText("Hello");
                
                hbox = new HBox();
                
                amount = new TextField();
                amount.prefWidthProperty().set(100);
                amount.promptTextProperty().set("Amount (opt)");
                
                unit = new TextField();
                unit.prefWidthProperty().set(70);
                unit.promptTextProperty().setValue("Unit (opt)");
                
                text = new Label();
                
                
                //configure layout
                //the HBox will hold both the amount and the item description
                hbox.spacingProperty().set(10);
                hbox.alignmentProperty().set(Pos.CENTER_LEFT);
                hbox.getChildren().addAll(amount, unit, text);
                this.setGraphic(hbox);
                
                this.amount.focusedProperty().addListener((e) ->
                {
                    this.amountFocusChanged();
                });
                
                this.unit.focusedProperty().addListener((e) ->
                {
                    this.unitFocusChanged();
                });
            }
            
            private void amountFocusChanged()
            {
                //if the amount has lost focus and (the current item is a Recipe or the amount isn't blank)
                boolean a = CookBook.isRecipe(this.itemProperty().get().getName());
                boolean b = !this.amount.getText().trim().equals("");
                if (this.amount.focusedProperty().getValue() == false && (CookBook.isRecipe(this.itemProperty().get().getName()) || !"".equals(this.amount.getText().trim())))
                {
                    //focus was just lost...
                    //check the value
                    if (Helper.isNumeric(this.amount.getText()))
                    {
                        //adjust this item's amount...
                        this.itemProperty().get().setAmount(Float.parseFloat(this.amount.getText()));
                    }
                    
                    //refresh the display...
                    this.amount.setText(this.itemProperty().get().getAmount().toString());
                }
            }
            
            private void unitFocusChanged()
            {
                if (this.unit.focusedProperty().getValue() == false)
                {
                    //update the unit...
                    this.itemProperty().get().setUnit(this.unit.getText());
                }
            }
            
            @Override
            public void updateItem(IShoppingEntry item, boolean empty)
            {
                super.updateItem(item, empty);
                
                if (item != null)
                {
                    this.setGraphic(hbox);
                    
                    //check if it is a Recipe or an Ingredient and adjust accordingly...
                    if (CookBook.isRecipe(item.getName()))
                    {                        
                        //make this bold if it is a Recipe
                        this.setStyle("-fx-font-weight: bold");
                        
                        this.amount.setText(item.getAmount().toString());
                        this.unit.setText("Servings");
                        this.unit.disableProperty().setValue(true);
                    }
                    else
                    {
                        //set fields to proper values
                        this.amount.setText((item.getAmount() == 0f) ? "" : item.getAmount().toString());
                        this.unit.setText(item.getUnit());
                        
                        this.unit.disableProperty().setValue(false);
                    }
                    
                    this.text.setText(item.getName());
                }
                else
                {
                    //this.setText("");
                    this.unit.visibleProperty().setValue(true);
                    this.text.setText("");
                    this.setStyle("-fx-font-weight: normal");
                    this.setGraphic(null);
                }
            }
        };
        
        return cell;   
    }
}
