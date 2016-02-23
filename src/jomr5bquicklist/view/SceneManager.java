/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
////////////////////////////////
//CODE CREATED BY DALE MUSSER!!!
package jomr5bquicklist.view;

import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author dale
 */
public class SceneManager
{
    private Scene scene;
    private final HashMap<String, Switchable> controllers = new HashMap<>();

    public SceneManager()
    {

    }

    public SceneManager(Scene scene)
    {
        this.scene = scene;
    }

    public void setScene(Scene scene)
    {
        this.scene = scene;
    }

    public Scene getScene()
    {
        return scene;
    }

    public Switchable add(String name)
    {
        Switchable controller;

        controller = controllers.get(name);

        if (controller == null)
        {
            try
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(name + ".fxml"));
                Parent root = (Parent) loader.load();
                controller = (Switchable) loader.getController();
                controller.setRoot(root);
                controller.setSceneManager(this);
                controllers.put(name, controller);
            } 
            catch (Exception ex)
            {
                controller = null;
            }
        }

        return controller;
    }

    public void switchTo(String name)
    {
        Switchable controller = controllers.get(name);

        if (controller == null)
        {
            controller = add(name);
        }

        if (controller != null)
        {
            if (scene != null)
            {
                scene.setRoot(controller.getRoot());

                //notify controller of entrance
                controller.focusEnter();
            }
        }
    }
}
