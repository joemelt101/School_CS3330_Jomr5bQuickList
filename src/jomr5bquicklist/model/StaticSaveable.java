/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bquicklist.model;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Jared
 */
public abstract class StaticSaveable
{
    protected static void save(String url, String data)
    {
        Path path = Paths.get(url);
        
        if (Files.isWritable(path))
        {
            //attempt write with dispose
            try (FileWriter writer = new FileWriter(url))
            {
                writer.write(data);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
    
    protected static List<String> load(String url)
    {
        Path path = Paths.get(url);
        
        if (Files.isWritable(path))
        {
            try
            {
                //no closing necessary!
                return Files.readAllLines(path);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        
        return null;
    }
}
