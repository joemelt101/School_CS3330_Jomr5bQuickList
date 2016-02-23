/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import jomr5bquicklist.Settings;
import jomr5bquicklist.model.CookBook;
import jomr5bquicklist.model.IngredientEntry;
import jomr5bquicklist.model.Recipe;
import jomr5bquicklist.view.IngredientEntryCellFactory;
import jomr5bquicklist.view.Switchable;

/**
 * FXML Controller class
 *
 * @author Jared
 */
public class CookBookController extends Switchable implements Initializable
{   
    ////////////
    //Properties
    String selectedName = "";
    String selectedIngredient = "";
    TitledPane currentSelectedPane;
    
    /////////////
    //Controls...
    
    @FXML
    public TextField searchTextField;
    
    @FXML
    public ListView cookBookList;
    
    @FXML
    public TextField recipeTitle;
    
    @FXML
    public TextField numberOfServings;
    
    @FXML
    public TextField ingredientName;
    
    @FXML
    public TextField amountOfIngredient;
    
    @FXML
    public TextField unit;
    
    @FXML
    public ListView currentIngredients;
    
    @FXML
    public TextField ingredientNameBottom;
    
    @FXML
    public Accordion accordion;
    
    @FXML
    public Text ingredientErrorText;
    
    @FXML
    public Text recipeErrorText;
    
    ////////////////////
    //Event Handlers....
    
    @FXML
    public void handleShoppingListButtonClicked(ActionEvent event)
    {
        this.getSceneManager().switchTo("Home");
    }
    
    @FXML
    public void handleAboutButtonClicked(ActionEvent event)
    {
        Helper.displayAboutPage();
    }
    
    @FXML
    public void handleAddIngredientButtonClicked(ActionEvent event)
    {
        if (this.fieldsAreValid())
        {
            Recipe rec = CookBook.getRecipe(this.recipeTitle.getText());

            //if a new recipe is being created...
            if (rec == null)
            {
                //create a new recipe and retrieve it from the CookBook
                CookBook.addRecipe(this.recipeTitle.getText(), Float.parseFloat(this.amountOfIngredient.getText()), null);
                rec = CookBook.getRecipe(this.recipeTitle.getText());
            }

            rec.addIngredient(this.ingredientName.getText(), Float.parseFloat(this.amountOfIngredient.getText()), this.unit.getText());

            //refresh display...
            this.openRecipe(this.recipeTitle.getText());
            this.showAllNames();

            //set current selection to the open name
            this.cookBookList.getSelectionModel().select(this.recipeTitle.getText());

            //save data
            CookBook.saveCookBook(Settings.SAVE_LOCATION);

            //prevent further changes to the Recipe name
            this.selectedName = this.recipeTitle.getText();
            this.recipeTitle.setDisable(true);
        }
    }
    
    @FXML
    public void handleDeleteIngredientFromRecipeButtonClicked(ActionEvent event)
    {
        //get the current ingredient
        IngredientEntry entry = (IngredientEntry) this.currentIngredients.getSelectionModel().getSelectedItem();
        Recipe rec = CookBook.getRecipe(this.recipeTitle.getText());
        rec.removeIngredient(entry.getName());
        
        //save to the harddrive
        CookBook.saveCookBook(Settings.SAVE_LOCATION);
        
        //refresh the display...
        this.openRecipe(rec.getName());
        this.showAllNames();
    }
    
    @FXML
    public void handleDeleteRecipeClicked(ActionEvent event)
    {
        if (!"".equals(this.selectedName))
        {
            //already exists so I can delete it
            CookBook.removeRecipe(this.selectedName);
        }
        
        //now clear the fields and refresh the display
        this.clearAllTextFields();
        this.showAllNames();
        
        //ensure the changes are saved
        CookBook.saveCookBook(Settings.SAVE_LOCATION);
    }
    
    @FXML
    public void handleAccordionClicked(MouseEvent event)
    {
        if (this.accordion.getExpandedPane()!= this.currentSelectedPane)
        {
            //change of panes detected!
            this.currentSelectedPane = this.accordion.getExpandedPane();
            handleAccordionChanged();
        }
    }
    
    private void handleAccordionChanged()
    {
        this.clearAllTextFields();
        this.cookBookList.getSelectionModel().clearSelection();
        this.selectedName = "";
    }
    
    @FXML
    public void handleAddIngredientToCookBookClicked(ActionEvent event)
    {
        //first validate...
        String text = this.ingredientNameBottom.getText().trim();
        if ("".equals(text) || this.reservedCharactersUsed(text))
        {
            //cannot process this!
            this.ingredientErrorText.setText("The ingredient name must be filled out and not contain '*' or '|'!");
            return;
        }
        
        CookBook.addIngredient(this.ingredientNameBottom.getText());
        
        //now refresh the display
        this.showAllNames();
    }
    
    @FXML
    public void handleRemoveIngredientFromCookBook(ActionEvent event)
    {
        //TODO Complete
        List<Recipe> res = CookBook.removeIngredient(this.ingredientNameBottom.getText());
        
        if (res != null)
        {
            //error, couldn't delete because a recipe is currently using it...
            this.ingredientErrorText.setText("Can't Delete! Currently in use by at lease one Recipe!");
        }
        else
        {
            //good to go
            //clear the text...
            this.ingredientErrorText.setText("");
            this.ingredientNameBottom.setText("");
        }
        
        CookBook.saveCookBook(Settings.SAVE_LOCATION);
        
        this.showAllNames();
    }
    
    @FXML
    public void handleNewButtonClicked(ActionEvent event)
    {
        this.cookBookList.getSelectionModel().clearSelection();
        this.selectedName = "";
        this.clearAllTextFields();
    }
    
    @FXML
    public void handleNameListViewClicked(MouseEvent event)
    {   
        String currentlySelectedName = (String) this.cookBookList.getSelectionModel().getSelectedItem();
        if (currentlySelectedName != null && !currentlySelectedName.equals(this.selectedName))
        {
            //there was a change in name selections!
            this.selectedName = currentlySelectedName;
            handleNameListViewSelectionChanged();
        }
    }
    
    private void handleNameListViewSelectionChanged()
    {
        this.clearAllTextFields();
        
        //adjust selectedName
        if (this.selectedName.startsWith("*"))
        {
            //it is an ingredient
            this.selectedName = this.selectedName.substring(1, this.selectedName.length());
        }
            
        //Populate the fields with data since there was a selection change
        //figure out if an ingredient or not...
        if (CookBook.isIngredient(this.selectedName) == true)
        {
            this.openIngredient(this.selectedName);
        }
        else
        {
            this.openRecipe(this.selectedName);
        }
    }
    
    @FXML
    public void handleIngredientListViewClicked(MouseEvent event)
    {
        IngredientEntry currentlySelectedEntry = (IngredientEntry) this.currentIngredients.getSelectionModel().getSelectedItem();
        if (currentlySelectedEntry != null && !this.selectedIngredient.equals(currentlySelectedEntry.getName()))
        {
            //there was a change in name selections!
            this.selectedIngredient = currentlySelectedEntry.getName();
            handleIngredientListViewSelectionChanged();
        }
    }
    
    private void handleIngredientListViewSelectionChanged()
    {
        Recipe rec = CookBook.getRecipe(this.recipeTitle.getText());
        IngredientEntry entry = rec.getIngredients().get(this.selectedIngredient);
        this.ingredientName.setText(this.selectedIngredient);
        this.amountOfIngredient.setText(entry.getAmount().toString());
        this.unit.setText(entry.getUnit());
    }
    
    private void handleFocusChangeOnServings()
    {
        if (this.numberOfServings.focusedProperty().getValue() == false)
        {
            //check that a number is inside the field...
            if (Helper.isNumeric(this.numberOfServings.getText()) == false)
            {
                //place default value...
                //and notify user of problem...
                this.numberOfServings.setText("1");
                this.recipeErrorText.setText("There must be a numeric value inside of the servings field!");
                return;
            }
            else
            {
                //the value is numeric, ensure it isn't 0!
                Float val = Float.parseFloat(this.numberOfServings.getText());
                if (val == 0)
                {
                    this.numberOfServings.setText("1");
                    this.recipeErrorText.setText("This must be at least 1 serving!");
                    return;
                }
            }

            //if the numberOfServings text field has just lost focus 
            //and the Recipe already exists, then I need to save it!
            if (this.cookBookList.getSelectionModel().getSelectedItem() != null)
            {
                //just lost focus...
                Recipe rec = CookBook.getRecipe(this.selectedName);
                rec.setServingSize(Float.parseFloat(this.numberOfServings.getText()));

                CookBook.saveCookBook(Settings.SAVE_LOCATION);

                this.openRecipe(rec.getName());
            }
        }
    }
    
    @FXML
    private void handleHelpButtonClicked(ActionEvent event)
    {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Help - CookBook");
        a.setHeaderText("Help for the CookBook Page");
        String text = "This is where you manage Recipes and Ingredients!"
                + "\n\nTo add a Recipe:"
                + "\n\t1) Open 'Add New or Edit Selected Recipe'"
                + "\n\t2) Add information as prompted"
                + "\n\t3) Press the '+' button to add the ingredient. Note: If this is"
                + "the first ingredient your adding, the Recipe will be saved and the Title will be unchangeable."
                + "\n\t4) Add additional ingredients until the Recipe is complete"
                + "\n\nRemoving a Recipe:"
                + "\n\t1) Click the Recipe you want to Remove (will be one without an '*')"
                + "\n\t2) Click 'Delete Recipe' button"
                + "\n\nAdd an Ingredient:"
                + "\n\t1) Click the 'Add or Edit Selected Ingredients' Pane"
                + "\n\t2) Enter information and click 'Save Ingredient'"
                + "\n\nRemove an Ingredient:"
                + "\n\t1) Select the Ingredient to Remove on the Left Pane"
                + "\n\t2) Click 'Delete Ingredient'"
                + "\n\tNote: It will only delete if it's not being currently used by a Recipe...";
        a.setContentText(text);
        a.showAndWait();
    }
    
    private void handleValueChangeOnSearchBox()
    {
        this.showAllNames();
        
        String text = this.searchTextField.getText();
        
        //filter out results if necessary
        if (!text.equals(""))
        {
            ObservableList<String> list = this.cookBookList.getItems();
            this.cookBookList.setItems(list.filtered((i) -> 
            {
                return i.toLowerCase().contains(text.toLowerCase());
            }));
        }
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        showAllNames();
        
        //go ahead and show the recipe pane
        this.showRecipePane();
        this.currentSelectedPane = this.accordion.getExpandedPane();
        
        //listen for changes in the serving size TextField as to save changes
        this.numberOfServings.focusedProperty().addListener(
        (e) ->
        {
            this.handleFocusChangeOnServings();
        }
        );
        
        //listen for changes in the search box
        this.searchTextField.textProperty().addListener(
        (e) ->
        {
            this.handleValueChangeOnSearchBox();
        }
        );
    }
    
    @Override
    public void focusEnter()
    {
        //basic refresh of page...
        this.showAllNames();
        this.clearAllTextFields();
        this.cookBookList.getSelectionModel().clearSelection();
        this.selectedName = "";
    }
    
    //////////////////
    //Helper Functions
    
    private void showAllNames()
    {
        //Initialize the cookBookList
        Set<String> s = CookBook.getIngredients().keySet();
        List<String> list = new ArrayList<>(s);
        
        for (int i = 0; i < list.size(); ++i)
        {
            list.set(i, "*" + list.get(i));
        }
        
        s = CookBook.getRecipes().keySet();
        
        list.addAll(s);
        list.sort((l, r) -> { return l.compareTo(r); });
        ObservableList<String> oList = FXCollections.observableArrayList(list);
        this.cookBookList.setItems(oList);
    }
    
    private void openRecipe(String name)
    {
        this.clearAllTextFields();
        this.showRecipePane();
        
        //opened a recipe
        Recipe rec = CookBook.getRecipe(name);
        this.recipeTitle.setText(rec.getName());
        this.numberOfServings.setText(rec.getServingSize().toString());

        //Now set current ingredients
        ObservableList<IngredientEntry> arrayList = FXCollections.observableArrayList(rec.getIngredients().values());
        this.currentIngredients.setItems(arrayList);
        this.currentIngredients.setCellFactory(new IngredientEntryCellFactory());
        
        //ensure no changes to the title are made...
        this.recipeTitle.setDisable(true);
    }
    
    private void openIngredient(String name)
    {
        this.clearAllTextFields();
        this.ingredientNameBottom.setText(name);
        
        this.showIngredientPane();
    }
    
    private void clearAllTextFields()
    {
        this.amountOfIngredient.setText("");
        this.ingredientName.setText("");
        this.ingredientNameBottom.setText("");
        this.numberOfServings.setText("");
        this.recipeTitle.setText("");
        this.searchTextField.setText("");
        this.unit.setText("");
        this.currentIngredients.setItems(null);
        
        //also open up name editing
        this.recipeTitle.setDisable(false);
        
        //also remove error message from ingredient name issues and Recipe issues
        this.ingredientErrorText.setText("");
        this.recipeErrorText.setText("");
    }
    
    private void showRecipePane()
    {
        //show pane
        TitledPane nextPane = this.accordion.getPanes().get(0);
        this.accordion.setExpandedPane(nextPane);
        this.currentSelectedPane = nextPane;
    }
    
    private void showIngredientPane()
    {
        TitledPane nextPane = this.accordion.getPanes().get(1);
        this.accordion.setExpandedPane(nextPane);
        this.currentSelectedPane = nextPane;
    }
    
    private boolean fieldsAreValid()
    {
        //Check all the fields are filled
        if ("".equals(this.recipeTitle.getText().trim()))
        {
            this.recipeErrorText.setText("You must have a title for your recipe!");
        }
        
        if ("".equals(this.numberOfServings.getText().trim()))
        {
            this.recipeErrorText.setText("Must have a number in the number of servings text box!");
            return false;
        }
        
        if ("".equals(this.ingredientName.getText()))
        {
            this.recipeErrorText.setText("There must be an ingredient name before you can submit!");
            return false;
        }
        
        if ("".equals(this.amountOfIngredient.getText().trim()))
        {
            this.recipeErrorText.setText("Must have a number in the amount of ingredient text box!");
            return false;
        }
        
        if ("".equals(this.unit.getText().trim()))
        {
            this.recipeErrorText.setText("There must be a unit typed into the unit box!");
            return false;
        }
        
        //check that number fields contain numbers
        if (Helper.isNumeric(this.numberOfServings.getText()) == false)
        {
            this.recipeErrorText.setText("There must be a number inside of the servings field!");
            return false;
        }
        
        if (Helper.isNumeric(this.amountOfIngredient.getText()) == false)
        {
            this.recipeErrorText.setText("There must be a number inside of the amount for an ingredient!");
            return false;
        }
        
        //check that special characters are avoided
        if (this.reservedCharactersUsed(this.recipeTitle.getText()))
        {
            this.recipeErrorText.setText("The recipe title contains a reserved character. Cannot use '*' or '|'!");
            return false;
        }
        
        if (this.reservedCharactersUsed(this.ingredientName.getText()))
        {
            this.recipeErrorText.setText("The ingredient name contains a reserved character. Cannot use '*' or '|'!");
            return false;
        }
        
        if (this.reservedCharactersUsed(this.unit.getText()))
        {
            this.recipeErrorText.setText("The unit contains a reserved character. Cannot use '*' or '|'!");
            return false;
        }
        
        return true;
    }
    
    private boolean reservedCharactersUsed(String str)
    {
        if (str.contains("*") || str.contains("|"))
        {
            return true;
        }
        
        return false;
    }
}
