package pl.psnc.nina.harvester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Parser {

    public int i;
    public List<String> webPage = new ArrayList<>();
    private int numberOfPages;

    private List filmList = new ArrayList(); //nowe odczytane filmy
    private List categories = new ArrayList();
    private Map<String, Film> mapOfFilms = new LinkedHashMap<>();

    public Parser() throws IOException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InterruptedException, InvalidFormatException {
        CategoriesConfig c = new CategoriesConfig();
        categories = c.getCategories();
        odczyt();
    }

    public void odczyt() throws IOException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InterruptedException, InvalidFormatException {

        setNumberOfPages("http://ninateka.pl/filmy?page=1");
        System.out.println("number of pages: " + numberOfPages);
        creatingAddresses();

        excel e = new excel();
        mapOfFilms = e.readCsv();

        odczytajDaneIZapisz(this, e);
    }

    /**
     *
     * @param t
     * @param e
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InterruptedException
     */
    public void odczytajDaneIZapisz(Parser t, excel e) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException {

        long start = System.currentTimeMillis();

        // !!!!!!!!
        for (int i = 1; i < 2; i++) {

            t.parsePage(webPage.get(i));
            e.readCsv();
            e.writeCsv(filmList);
            filmList.clear();
        }
        long end = System.currentTimeMillis();
        System.out.println("total time: " + (end - start));
    }

    /**
     * Parse element from web page 
     * taking data and data category from it
     * 
     * @param e2 one html <li> part //// ?????????? 
     * @return one categorie and data  name;Molier ????????????
     */
    public String zwrocKategorieIDane(Element e2) {
        String data = "";
        String categoryAndData = null;
        String[] temp = e2.toString().split("span>");

        if (temp.length > 1) {
            int o = temp[1].indexOf("<");
            categoryAndData = temp[1].substring(0, o);
        }

        // data
        if (temp.length > 2) {
            if (temp[2].length() > 7) {
                String pre = temp[2].substring(0, 8);

                if (pre.equals(" <a href")) {

                    String html = temp[2];
                    String test;

                    Document doc = Jsoup.parse(html);
                    Elements content3 = doc.select("a");

                    for (Element e3 : content3) {
                        Node data1 = e3.unwrap();
                        String dataD = data1.toString();
                        if (dataD.length() >= 2) {
                            if (dataD.substring(0, 2).contains("\n")) {
                                dataD = dataD.substring(1, dataD.length());
                            }
                        }
                        data = data + "," + dataD;
                    }
                } else {
                    int o = temp[2].indexOf("<");

                    if (o != -1 && !temp[2].equals("<br></li>")) {
                        data = data + "," + temp[2].substring(1, o);
                    }
                }
            }
            categoryAndData = categoryAndData + data;
        }
        return categoryAndData;
    }

    /**
     * Setting fields of newly created film
     *
     * @param mul ?????????/
     * @param name
     * @param link
     * @param img
     * @param description
     * @param description2
     * @param tags
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public void createFilm(List<String> mul, String name, String link, String img, String description, String description2, String tags) throws IOException, IllegalArgumentException, IllegalAccessException {

        Film film = new Film();
        Document d2 = Jsoup.connect(link).get();

        int index = name.indexOf("|");
        if (index != -1) {
            name = name.substring(0, index);
        }

        for (int j = 0; j < mul.size(); j++) {
            if (mul.get(j) != null) {
                String part = mul.get(j);
                if (part != null) {
                    int pole = part.indexOf(":");

                    if (pole != -1 && pole + 1 < part.length()) {

                        switch (part.substring(0, pole)) {
                            case "czas trwania":
                                film.setDuration(part.substring(pole + 2, part.length()));
                                break;
                            case "lektor":
                                film.setLector(part.substring(pole + 2, part.length()));
                                break;
                            case "kategoria":
                                film.setCategory(part.substring(pole + 2, part.length()));
                                break;
                            case "gatunek":
                                film.setGenre(part.substring(pole + 2, part.length()));
                                break;
                            case "linkPriv":
                                film.setLink(part.substring(pole + 2, part.length()));
                                break;
                            case "język":
                                film.setLanguage(part.substring(pole + 2, part.length()));
                                break;
                            case "reżyseria":
                                film.setDirectory(part.substring(pole + 2, part.length()));
                                break;
                            case "scenariusz":
                                film.setScenario(part.substring(pole + 2, part.length()));
                                break;
                            case "rok produkcji":
                                film.setProductionYear(part.substring(pole + 2, part.length()));
                                break;
                            case "producent":
                                film.setProducer(part.substring(pole + 2, part.length()));
                                break;
                            case "instrumentalista":
                                film.setInstrumentalist(part.substring(pole + 2, part.length()));
                                break;
                            case "charakteryzacja":
                                film.setCharakteryzation(part.substring(pole + 2, part.length()));
                                break;
                            case "prowadzący":
                                film.setProwadzący(part.substring(pole + 2, part.length()));
                                break;
                            case "barwa":
                                film.setColor(part.substring(pole + 2, part.length()));
                                break;
                            case "jakość":
                                film.setQuality(part.substring(pole + 2, part.length()));
                                break;
                            case "kategoria wiekowa":
                                if (pole + 2 >= part.length()) {
                                } else {
                                    film.setKategoriaWiekowa(part.substring(pole + 2, part.length()));
                                }
                                break;
                            case "uczestnik":
                                film.setParticipant(part.substring(pole + 2, part.length()));
                                break;
                            case "aktor":
                                film.setActor(part.substring(pole + 2, part.length()));
                                break;
                            case "zdjęcia":
                                film.setZdjęcia(part.substring(pole + 2, part.length()));
                                break;
                            case "realizacja":
                                film.setRealizacja(part.substring(pole + 2, part.length()));
                                break;
                            case "montaż":
                                film.setEditingSession(part.substring(pole + 2, part.length()));
                                break;
                            case "scenografia":
                                film.setScenography(part.substring(pole + 2, part.length()));
                                break;
                        }
                    }
                }
            }
        }

        film.setName(name);
        film.setImage(img);
        film.setLink(link);
        film.setDescription(description);
        film.setDescrition2(description2);
        film.setTags(tags);

        for (Field fff : film.getClass().getDeclaredFields()) {
            fff.setAccessible(true);
            if (fff.get(film) == null) {
                fff.set(film, "");
            }
        }

        filmList.add(film);
    }

    /**
     * Reads how many pages are on navigation panel sets numberOfPages
     *
     * @param adrStrony
     * @throws IOException
     */
    public void setNumberOfPages(String adrStrony) throws IOException {
        Document d = Jsoup.connect(adrStrony).get();
        int[] nrPages = new int[20];

        Elements elem = d.select(".span6.pagination");
        int i = 0;

        for (Element e : elem.select("a")) {
            String lastPage = e.toString();
            lastPage = lastPage.substring(21, lastPage.length());
            if (lastPage.indexOf("\"") != -1) {
                String[] c = lastPage.split("\"");
                c = c[0].split(" ");
                lastPage = c[0];
            }
            nrPages[i] = Integer.parseInt(lastPage);
            i++;
        }
        for (int n : nrPages) {
            if (n > numberOfPages) {
                numberOfPages = n;
            }
        }
    }

    /**
     * Parsing data form web page calls ??????? the method createFilm
     *
     * @param webPageAddress addres of web page that is going to be parsed
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public void parsePage(String webPageAddress) throws IllegalArgumentException, IllegalAccessException, IOException {

        String link;
        String img = "", tag = "";
        String opis = "", opis2 = "";
        String prefix = "http://ninateka.pl";

        List<String> categoriesAndData = new ArrayList<>();

        try {
            Document d = Jsoup.connect(webPageAddress).get();
            // int[] nrStron = new int[20];

            Elements content = d.select(".span12.movieListS ul li");
            for (Element e : content) {
                img = e.select("img").attr("src");

                String[] img2 = img.split("m=crop&w=150&h=112");
                img2[0] = img2[0].substring(0, img2[0].length() - 1);
                img = img2[0];

                //--------------- odczyt z drugiej strony ---------------
                // link do drugiej strony
                link = prefix.concat(e.select("a").unwrap().attr("href"));
                if (!mapOfFilms.containsKey(link)) {

                    Document d2 = Jsoup.connect(link).get();
                    Elements content2 = d2.select(".description");

                    Document d4 = Jsoup.connect(link).get();
                    Elements content4 = d4.select(".container");
                    content4 = content4.select(".style_h1");

                    String nazwa = "";
                    for (Element e2 : content4) {
                        nazwa = e2.text();
                    }

                    // categories
                    for (Element e2 : content2.select("#tabs-1 li")) {
                        String pom = zwrocKategorieIDane(e2);
                        if (pom != null) {
                            categoriesAndData.add(zwrocKategorieIDane(e2));
                        }
                    }

                    // second tab
                    for (Element e2 : content2.select("#tabs-2 li")) {
                        String secondTab = zwrocKategorieIDane(e2);
                        if (secondTab != null) {
                            categoriesAndData.add(secondTab);
                        }
                    }

                    // description
                    for (Element e2 : content2.select(".rawdescription.read-ivona")) {
                        opis = e2.text();
                    }

                    // tags
                    for (Element e2 : content2.select("#tags li")) {

                        String tag1 = "";
                        tag1 = e2.toString();
                        String[] ttt = tag1.split("<");
                        String[] tttt = ttt[2].split(">");

                        if (tttt.length >= 2) {
                            tag1 = tttt[1];

                            if (tag.equals("")) {
                                tag = "#" + tag1;
                            } else {
                                tag = tag + " #" + tag1;
                            }
                        }
                    }

                    // short description
                    Elements content3 = d2.select(".movie-description-text");
                    opis2 = content3.select("div").text();

                    createFilm(categoriesAndData, nazwa, link, img, opis, opis2, tag);

                    categoriesAndData.clear();
                    nazwa = "";
                    link = "";
                    img = "";
                    opis = "";
                    opis2 = "";
                    tag = "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates all the addresses to pages difhwoehfwohwofjep on Ninateka
     * W)wfoowfoepfjwpjpw ????????????????????
     */
    public void creatingAddresses() {
        String url1 = "http://ninateka.pl/filmy?page=1";
        String[] adr = url1.split("1");
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < numberOfPages; i++) {
            url1 = adr[0].concat("" + (i + 1));
            list.add(url1);
        }
        webPage = list;
    }
}
