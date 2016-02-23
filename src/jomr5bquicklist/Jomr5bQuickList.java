/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist;

import javafx.application.Application;
import static javafx.application.Application.STYLESHEET_MODENA;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jomr5bquicklist.model.CookBook;
import jomr5bquicklist.view.SceneManager;

/**
 *
 * @author Jared
 */
public class Jomr5bQuickList extends Application
{
    
    @Override
    public void start(Stage stage) throws Exception
    {
        // UI if SceneManager can't switch to a Scene
        HBox root = new HBox();
        root.setPrefSize(600, 400);
        root.setAlignment(Pos.CENTER);
        Text message = new Text("Error Loading Scene...");
        message.setFont(Font.font(STYLESHEET_MODENA, 32));
        root.getChildren().add(message);
        
        // create Scene and init UI to show failure in case switch fails
        Scene scene = new Scene(root);
        
        //Load in CookBook Data
        CookBook.loadCookBook(Settings.SAVE_LOCATION);
        
        //Open Initial Scene
        SceneManager sceneManager = new SceneManager(scene);
        sceneManager.switchTo("Home");
        
        stage.setScene(scene);
        stage.setTitle("QuickList");
        stage.getIcons().add(new Image(Jomr5bQuickList.class.getResourceAsStream("view/QuickListLogo.png")));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
