/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintQuality;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import jomr5bquicklist.Settings;
import jomr5bquicklist.model.CookBook;
import jomr5bquicklist.model.IShoppingEntry;
import jomr5bquicklist.model.IngredientEntry;
import jomr5bquicklist.model.RecipeEntry;
import jomr5bquicklist.view.IShoppingEntryEditableCellFactory;
import jomr5bquicklist.view.IShoppingEntryReadOnlyCellFactory;
import jomr5bquicklist.view.StringPrintTemplateCellFactory;
import jomr5bquicklist.view.Switchable;

/**
 *
 * @author Jared
 */
public class HomeController extends Switchable implements Initializable
{    
    @FXML
    public TextField searchField;
    
    @FXML
    public ListView addableList;
    
    @FXML
    public ListView shoppingList;
    
    @FXML
    public void handleAboutClicked(ActionEvent event)
    {
        Helper.displayAboutPage();
    }
    
    @FXML
    public void handleAddSelectedToListClicked(ActionEvent event)
    {
        if (this.addableList.getSelectionModel().getSelectedItem() != null)
        {
            this.transferIShoppingEntry(addableList, this.shoppingList, ((IShoppingEntry)this.addableList.getSelectionModel().getSelectedItem()).getName());
        }
    }
    
    @FXML
    public void handleClearListClicked(ActionEvent event)
    {
        for (IShoppingEntry entry : (ObservableList<IShoppingEntry>) this.shoppingList.getItems())
        {
            //move all of them back
            this.addableList.getItems().add(entry);
        }
        
        //sort the addableList
        ((ObservableList<IShoppingEntry>)this.addableList.getItems()).sort((l, r) -> { return l.getName().compareTo(r.getName()); });
        
        //clear the shopping list....
        this.shoppingList.setItems(FXCollections.observableArrayList());
    }
    
    @FXML
    public void handleCookBookButtonClicked(ActionEvent event)
    {
        this.getSceneManager().switchTo("CookBook");
    }
    
    @FXML
    public void handlePrintButtonClicked(ActionEvent event)
    {
        //go through items in the Shopping List Section
        ObservableList<IShoppingEntry> shoppingList = this.shoppingList.getItems();
        
        //Get all Ingredients
        ArrayList<IngredientEntry> ingredientEntries = new ArrayList<>();
        
        for (IShoppingEntry currentShoppingEntry : shoppingList)
        {
            if (CookBook.isRecipe(currentShoppingEntry.getName()))
            {
                //found a Recipe!
                RecipeEntry rec = (RecipeEntry)currentShoppingEntry;
                
                for (IngredientEntry iEntry : rec.getIngredientEntries())
                {
                    ingredientEntries.add(iEntry);
                }
            }
            else
            {
                //Must be an ingredient
                ingredientEntries.add((IngredientEntry)currentShoppingEntry);
            }
        }
        
        //now sort by name
        ingredientEntries.sort((left, right) ->
        {
            return left.getName().compareTo(right.getName());
        });
        
        //now sort via unit
        ingredientEntries.sort((left, right) ->
        {
           return left.getUnit().compareTo(right.getUnit());
        });
        
        //now merge like units
        for (int i = 0; i < ingredientEntries.size() - 1; ++i)
        {
            IngredientEntry firstEntry = ingredientEntries.get(i);
            IngredientEntry secondEntry = ingredientEntries.get(i + 1);
            
            if (firstEntry.getName().equals(secondEntry.getName()))
            {
                if (firstEntry.getUnit().toLowerCase().equals(secondEntry.getUnit().toLowerCase()))
                {
                    //found a matching unit!
                    //merge together
                    firstEntry.setAmount(firstEntry.getAmount() + secondEntry.getAmount());

                    //delete second entry
                    ingredientEntries.remove(i + 1);
                }
            }
        }
        
        //now the lines are created so do something with them!
        try (FileWriter writer = new FileWriter(Settings.OUTPUT_FILE_LOCATION))
        {
            writer.write("Shopping List\r\n");
            for (IngredientEntry entry : ingredientEntries)
            {
                String name = entry.getName();
                String amount = entry.getAmount() == 0f ? "" : entry.getAmount().toString();
                String unit = entry.getUnit();
                writer.write("[ ] " + name + " " + amount + " " + unit + "\r\n");
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }
        
        //now open the file
        File file = new File(Settings.OUTPUT_FILE_LOCATION);
        
        try
        {
            Desktop.getDesktop().open(file);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void handleSearchBoxChanged()
    {
        
        String text = this.searchField.getText();
        
        //get all of the names
        ObservableList<IShoppingEntry> list = FXCollections.observableArrayList();
        list.addAll(CookBook.getAllEntries());
        
        //filter out results if necessary
        if (!text.equals(""))
        {
            //filter out results that don't match the search text
            list = list.filtered((i) -> 
            {
                return i.getName().toLowerCase().contains(text.toLowerCase());
            });
        }
        
        //now filter out results that are currently added to the ShoppingList
        list = list.filtered((i) ->
        {
            ObservableList<IShoppingEntry> shoppingEntries = this.shoppingList.getItems();

            for (IShoppingEntry entry : shoppingEntries)
            {
                if (entry.getName().equals(i.getName()))
                {
                    //remove it!
                    return false;
                }
            }

            return true;
        });
        
        //replace the Observable list currently in the addableList
        ObservableList<IShoppingEntry> entries = FXCollections.observableArrayList();
        entries.addAll(list);
        this.addableList.setItems(entries);
    }
    
    @FXML
    public void handleListDragDetected(MouseEvent event)
    {
        Dragboard db = ((ListView)event.getSource()).startDragAndDrop(TransferMode.MOVE);
        
        ClipboardContent content = new ClipboardContent();
        content.putString(((IShoppingEntry)((ListView)event.getSource()).getSelectionModel().getSelectedItem()).getName());
        
        db.setContent(content);
        
        event.consume();
    }
    
    @FXML
    public void handleListDragOver(DragEvent event)
    {
        if (event.getGestureSource() != event.getSource() && event.getDragboard().hasString())
        {
            //good to go!
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }
    
    @FXML
    public void handleHelpButtonClicked(ActionEvent event)
    {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Help - Shopping List");
        a.setHeaderText("Help for the Shopping List Page");
        a.setWidth(600);
        String text = "Adding an Item:\n"
                + "\t1) Drag from 'Recipes and Ingredients' and Release over 'Current Shopping List' OR\n"
                + "\t2) Select an Item and Click 'Add Selected to List'\n\n"
                + "Removing Items:\n"
                + "\tSimply Press the 'Clear List' button or Drag from the 'Current Shopping List' to 'Recipes and Ingredients'\n\n"
                + "Printing out the List\n"
                + "\tClick the 'Print off Shopping List' button to organize a shopping list with all inputted ingredients and recipes.\n\n"
                + "Additional Notes: "
                + "\n\tRecipes are shown as bold in the 'Recipes and Ingredients' section and Ingredients are shown as normal.";
        a.setContentText(text);
        a.showAndWait();
    }
    
    @FXML
    public void handleListDragDropped(DragEvent event)
    {
        this.transferIShoppingEntry((ListView)event.getGestureSource(), (ListView)event.getGestureTarget(), event.getDragboard().getString());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //Load in the CookBook
        CookBook.loadCookBook(Settings.SAVE_LOCATION);
        
        //initiate ListView factories
        this.addableList.setCellFactory(new IShoppingEntryReadOnlyCellFactory());
        this.shoppingList.setCellFactory(new IShoppingEntryEditableCellFactory());
        
        this.loadInShoppingCandidatesToList();
        
        this.searchField.textProperty().addListener((cl) ->
                {
                    this.handleSearchBoxChanged();
                });
    }
    
    @Override
    public void focusEnter()
    {
        this.clearCurrentShoppingList();
    }
    
    ////////////////
    //Helper Methods
    
    private void loadInShoppingCandidatesToList()
    {
        ObservableList<IShoppingEntry> list = FXCollections.observableArrayList();
        list.addAll(CookBook.getAllEntries());
        list.sort((l, r) -> { return l.getName().compareTo(r.getName()); });
        this.addableList.setItems(list);
    }
    
    private void clearCurrentShoppingList()
    {
        this.shoppingList.setItems(FXCollections.observableArrayList());
        this.loadInShoppingCandidatesToList();
    }
    
    private void transferIShoppingEntry(ListView source, ListView target, String itemToMove)
    {
        ObservableList<IShoppingEntry> sItems = (ObservableList<IShoppingEntry>)source.getItems();
        ObservableList<IShoppingEntry> tItems = (ObservableList<IShoppingEntry>)target.getItems();
        
        //get and remove the item from the old list
        IShoppingEntry item = null;
        
        for (int i = 0; i < sItems.size(); ++i)
        {
            if (sItems.get(i).getName().equals(itemToMove))
            {
                //found it!
                item = sItems.get(i);
                sItems.remove(i);
                break;
            }
        }
        
        if (item != null)
        {
            //now add to the target ListView
            tItems.add(item);
            
            //be sure to sort!
            tItems.sort((l, r) -> { return l.getName().compareTo(r.getName()); });
        }
    }
}
