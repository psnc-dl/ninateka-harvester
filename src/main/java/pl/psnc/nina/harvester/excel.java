package pl.psnc.nina.harvester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class excel {

    private Sheet arkusz;
    private Workbook workbook;

    private List<String> configCategories = new ArrayList();
    private Map<String, Film> mapaFilmow = new HashMap();
    private final String NEW_LINE_SEPARATOR = "\n";
    private final String COMMA_DELIMITER = ";";
    private final String fileName = "mi.csv";
    private final File file;
    private List<Integer> indexToDelete = new ArrayList<>();

    
    public excel() throws FileNotFoundException, IOException {
        file = new File(fileName);
    }
/**
 * Reads all the movies that has been written to result file
 * before. Adding them to map. 
 * 
 *  
 * @return  map of movies 
 * @throws FileNotFoundException
 * @throws IOException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws NoSuchFieldException 
 */
    public Map<String, Film> readCsv() throws FileNotFoundException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {

        boolean first = true;
        String line = "";
        String cvsSplitBy = ";";
        BufferedReader br = null;
        FileWriter writer = new FileWriter(fileName, true);

        CategoriesConfig c = new CategoriesConfig();
        configCategories = c.getCategories();
        String[] header = null;

        try {
            if (!file.exists()) {
                System.out.println("File does not exist!");
            } else {
                br = new BufferedReader(new FileReader(fileName));

                while ((line = br.readLine()) != null) {
                    if (first == true) {
                        first = false;
                        System.out.println(line);
                        header = line.split(";");
                    } else {
                        String[] filmInfo = line.split(cvsSplitBy);
                        List<String> list = new ArrayList<>();

                        for (String d : filmInfo) {
                            list.add(d);
                        }
                        while (list.size() < configCategories.size() + 1) { //29) {
                            list.add("");
                        }

                        if (list.size() == 47015) {
                            System.out.println("NIO");
                        }

                        int j = 0;
                        Film f = new Film();

                        for (Field fff : f.getClass().getDeclaredFields()) {
                            if (configCategories.contains(fff.getName())) {
                                List<String> listC = Arrays.asList(header);
                                fff.setAccessible(true);
                                int index = listC.indexOf(fff.getName());
                                
                                fff.set(f, list.get(index));
                            }
                            j++;
                        }
                        mapaFilmow.put(f.getLink(), f);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
        return mapaFilmow;
    }

    
    /**
     * Deleting columns that aren't in configuration file
     * 
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException 
     * 
     */
    public void deleteColumns() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        String line;
        String fileN = "aaa.csv";
        File file = new File(fileName);
        
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), "UTF-8"));
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(fileName));
 
        while ((line = br.readLine()) != null) {

            String[] podz = line.split(";");
            List<String> aList = Arrays.asList(podz);

            for (int i : indexToDelete) {
                aList.remove(i);
            }
            for (String s : aList) {
                out.write(s);
                out.append(COMMA_DELIMITER);
            }
            out.append(NEW_LINE_SEPARATOR);
        }
    }

    /**
     * Writes films that have to be added to result file
     *
     * @param filmList - list of films to add
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    public void writeCsv(List<Film> filmList) throws FileNotFoundException, UnsupportedEncodingException, IOException, IllegalArgumentException, IllegalAccessException {

        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, true), "UTF-8"));

        BufferedReader br;
        br = new BufferedReader(new FileReader(fileName));

        // delete colums that are not needed
        deleteColumns();

        // header
        for (String a : configCategories) {
            out.append(a);
            out.append(COMMA_DELIMITER);
        }
        out.append(NEW_LINE_SEPARATOR);

        try {
            for (Film f : filmList) {
                for (Field fff : f.getClass().getDeclaredFields()) {
                    if (configCategories.contains(fff.getName())) {
                        if (!"".equals(fff.getName())) {
                            String value;
                            fff.setAccessible(true);

                            Object v = fff.get(f);
                            value = (String) v;

                            if (v == null) {
                                value = "";
                            }
                            out.write(value);
                            out.append(COMMA_DELIMITER);
                        }
                    }
                    System.out.println("OUT");
                }
                out.append(NEW_LINE_SEPARATOR);
            }
        } finally {
            out.close();
        }
    }
}
