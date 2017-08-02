package pl.psnc.nina.harvester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

public class CsvHelper {

    String fileName = "inputFile.csv";
    File file = new File(fileName);

    String outputFileName = "outputFile.csv";
    File outputFile = new File(outputFileName);

    List<String> configCategories;
    private final String NEW_LINE_SEPARATOR = "\n";
    private final String COMMA_DELIMITER = ";";

    private List<Integer> toDelete = new ArrayList<>();
    private String[] headerCategories;

    /**
     * Reads films from file that was parsed before
     *
     *
     * @return
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public Map<String, Film> readFile() throws FileNotFoundException, IllegalArgumentException, IllegalAccessException, IOException {

        String line;
        int counter = 0;
        Map<String, Film> filmMap = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        BufferedWriter bWriter
                = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName, false), "UTF-8"));

        CategoriesConfig c = new CategoriesConfig();
        configCategories = c.readConfiguration();

        boolean first = true;

        try {
            if (!file.exists()) {
                System.out.println("File does not exist!");
            } else {
                while ((line = br.readLine()) != null) {
                    if (first == true) {
                        first = false;

                        if (line.equals("")) {
                            // file was empty -> without a header 
                            headerCategories = new String[configCategories.size()];
                            int i = 0;
                            for (String cc : configCategories) {
                                headerCategories[i] = cc;
                                bWriter.append(cc);
                                bWriter.append(COMMA_DELIMITER);
                                i++;
                            }
                            bWriter.append(NEW_LINE_SEPARATOR);

                        } else {
                            headerCategories = line.split(";");

                            int index = 0;
                            int i = 0;

                            // checking if every category from configuration file is in header 
                            for (String h : headerCategories) {
                                if (configCategories.contains(h)) {
                                    bWriter.write(h);
                                    bWriter.write(COMMA_DELIMITER);
                                    counter++;
                                } else {
                                    // category that is not needed
                                    toDelete.add(i);
                                }
                                i++;
                            }
                            bWriter.write(NEW_LINE_SEPARATOR);

                            // checking if everything that is in configuration file is in file that was created before
                            if (configCategories.size() == counter) {
                            } else if ((counter + toDelete.size()) == configCategories.size()) {
                            } // there are columns to delete
                            else {
                                System.out.println("break !");
                                // to few columns, everything has to be parsed from the beginning
                                break;
                            }
                        }
                    } // rest of the file
                    else {
                        String[] dataLine = line.split(";");
                        List<String> dList = new ArrayList<>(Arrays.asList(dataLine));

                        while (dList.size() < configCategories.size()) {
                            dList.add("");
                        }

                        Film f = new Film();
                        for (int x = 0; x < dList.size(); x++) {
                            if (!toDelete.contains(x)) {
                                if (!(x >= headerCategories.length)) {
                                    Field fff = f.getClass().getDeclaredField(headerCategories[x]);

                                    fff.setAccessible(true);
                                    fff.set(f, dList.get(x));

                                    bWriter.write(dList.get(x));
                                    bWriter.write(COMMA_DELIMITER);
                                }
                            }
                        }

                        filmMap.put(f.getLink(), f);
                        bWriter.write(NEW_LINE_SEPARATOR);
                    }
                }
                bWriter.close();
                br.close();
            }

        } catch (IOException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            System.out.println("error");
        }
        return filmMap;
    }

    /**
     * Writes new films to outputFile.csv
     *
     * @param filmList
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public void writeCsv(List<Film> filmList) throws FileNotFoundException,
            UnsupportedEncodingException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {

        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName, true), "UTF-8"));
        System.out.println("writing new " + filmList.size() + " film/films");

        try {
            for (Film f : filmList) {
                for (Field fff : f.getClass().getDeclaredFields()) {
                    if (configCategories.contains(fff.getName())) {
                        fff.setAccessible(true);

                        out.write((String) fff.get(f));
                        out.write(COMMA_DELIMITER);
                    }
                }
                out.write(NEW_LINE_SEPARATOR);
            }
        } finally {
            out.close();
        }
        out.close();
    }

    public void changeFileName() {
        if (file.exists()) {
            file.delete();
        }
        boolean isMoved = outputFile.renameTo(new File("inputFile.csv"));
        System.out.println("renamed: " + isMoved);
    }
}
