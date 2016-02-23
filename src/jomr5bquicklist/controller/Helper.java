/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author Jared
 */
public class Helper
{
    public static boolean isNumeric(String str)
    {
        try  
        {  
            Float.parseFloat(str);  
        }  
        catch(NumberFormatException nfe)  
        {  
            return false;
        }  
        
        return true; 
    }
    
    public static void displayAboutPage()
    {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("About");
        a.setHeaderText("QuickList Application");
        a.setContentText("The Quick Shopping List Creator.\nCreated By Jared Melton for CS3330");
        a.showAndWait();
    }
}
