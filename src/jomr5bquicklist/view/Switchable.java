/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.view;

import javafx.scene.Parent;

/**
 *
 * @author dale
 */
public abstract class Switchable 
{
    private Parent root;
    private SceneManager sceneManager;
    
    public void setRoot(Parent root) 
    {
        this.root = root;
    }
    
    public Parent getRoot() 
    {
        return root;
    }
    
    public abstract void focusEnter();
    
    public void setSceneManager(SceneManager sceneManager) 
    {
        this.sceneManager = sceneManager;
    }
    
    public SceneManager getSceneManager() 
    {
        return sceneManager;
    }
}
