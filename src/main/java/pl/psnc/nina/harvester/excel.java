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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class excel {

    private Sheet arkusz;
    private Workbook workbook;

    private Map<String, Film> mapaFilmow = new HashMap();
    private final String NEW_LINE_SEPARATOR = "\n";
    private final String COMMA_DELIMITER = ";";
    private final String fileName = "ninatekaFilmy.csv";
    private File file;
    
    public excel() throws FileNotFoundException, IOException {
        // open existing
        file = new File(fileName);
    }

    public Map<String, Film> readCsv() throws FileNotFoundException, IOException {

        boolean first = true;
        String line = "";
        String cvsSplitBy = ";";
        BufferedReader br = null;     
        FileWriter writer = new FileWriter(fileName,true);
           
        try {          
            if (!file.exists()) {
                System.out.println("plik nie istnieje!");
            } else {
                br = new BufferedReader(new FileReader(fileName));

                while ((line = br.readLine()) != null) {
                    if (first == true) {
                        first = false;

                    } else {
                        int j = 0;
                        String[] daneFilmy = line.split(cvsSplitBy);
                        List<String> lista = new ArrayList<>();

                        for (String d : daneFilmy) {
                            lista.add(d);
                        }
                        while (lista.size() < 29) {
                            lista.add("");
                        }

                        Film f = new Film();
                        for (Field fff : f.getClass().getDeclaredFields()) {
                            fff.setAccessible(true);
                            fff.set(f, lista.get(j));
                            j++;
                        }
                        mapaFilmow.put(f.getLink(), f);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("blad");
        }

        return mapaFilmow;
    }

    public void writeCsv(List<Film> listaFilmow) throws FileNotFoundException, UnsupportedEncodingException, IOException, IllegalArgumentException, IllegalAccessException {

        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, true), "UTF-8"));

        BufferedReader br;
        br = new BufferedReader(new FileReader(fileName));

        if (br.readLine() == null) {
     
            // nagłówek
            Film ff = new Film();
            for (Field fff : ff.getClass().getDeclaredFields()) {
                out.write(fff.getName());
                out.append(COMMA_DELIMITER);
            }
            out.append(NEW_LINE_SEPARATOR);
        }

        try {

            for (Film f : listaFilmow) {

                for (Field fff : f.getClass().getDeclaredFields()) {
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
                out.append(NEW_LINE_SEPARATOR);
            }
        } finally {
            out.close();
        }
    }
}
