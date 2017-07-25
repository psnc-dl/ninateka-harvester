/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.psnc.nina.harvester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Laura
 */
public class CategoriesConfig {

    private File file;
    private String fileName = "config.txt";
    private List<String> categories;

    
    public CategoriesConfig() throws IllegalArgumentException, IllegalAccessException, IOException {
        file = new File(fileName);
        setCategories(readConfiguration());
    }

    /**
     * Reads categories of film from configurarion file (config.txt)
     * 
     * @return list of film categories  
     * @throws IOException 
     */
    public List<String> readConfiguration() throws IOException {
        String line;
        FileWriter fw = new FileWriter(file, true);
        ArrayList<String> categ = new ArrayList<>();

        try {
            if (file.exists()) {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                while ((line = reader.readLine()) != null) {
                    categ.add(line);
                }
                // link is obligatory, has to be in map of movies
                if (!categ.contains("link")) {
                    categ.add("link");
                }
            } else {
                BufferedWriter bw = new BufferedWriter(fw);
                Film ff = new Film();
                for (Field fff : ff.getClass().getDeclaredFields()) {
                    bw.append(fff.getName() + "\n");
                }
                bw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return categ;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}
