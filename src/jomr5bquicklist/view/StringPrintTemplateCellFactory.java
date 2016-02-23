/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.view;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 *
 * @author Jared
 */
public class StringPrintTemplateCellFactory implements Callback<ListView<String>, ListCell<String>>
{
    @Override
    public ListCell<String> call(ListView<String> param)
    {
        final ListCell<String> cell = new ListCell<String>()
        {
            private final CheckBox checkBox = new CheckBox();
            private final HBox hbox = new HBox();
            private final Text text = new Text();
            
            {
                super.setPrefWidth(60);
                
                //initiate extra stuff...
                this.hbox.getChildren().addAll(checkBox, text);
                this.hbox.spacingProperty().set(10);
                this.hbox.alignmentProperty().set(Pos.CENTER_LEFT);
                
                this.setStyle("-fx-background-color: white;");
            }
            
            @Override
            public void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);
                
                if (item != null)
                {
                    this.setGraphic(hbox);
                    this.text.setText(item);
                }
                else
                {
                    this.text.setText("");
                    this.setGraphic(null);
                }
            }
        };
        
        return cell;   
    }
}
