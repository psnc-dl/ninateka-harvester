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
    private Map<String, Film> mapOfFilms = new LinkedHashMap<>();

    public Parser() throws IOException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InterruptedException, InvalidFormatException {
        odczyt();
    }
/**
 * 
 * @throws IOException
 * @throws FileNotFoundException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws NoSuchFieldException
 * @throws InterruptedException
 * @throws InvalidFormatException 
 */
    public void odczyt() throws IOException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InterruptedException, InvalidFormatException {

        ustawIloscStron("http://ninateka.pl/filmy?page=1");
        System.out.println("number of pages: " + numberOfPages);
        zmianaAdresow();

        excel e = new excel();
        mapOfFilms = e.readCsv();

        odczytajDaneIZapisz(this, e);

    }

    public void odczytajDaneIZapisz(Parser t, excel e) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException {

        long start = System.currentTimeMillis();
     
        // !!!!!!!!
        for (int i = 0; i < 4; i++) {

            t.odczytStronIPodstron(webPage.get(i));
            e.writeCsv(filmList);

            filmList.clear();
        }
        long end = System.currentTimeMillis();

        System.out.println("total time: " + (end - start));
    }

    // zwraca jedna kategorie wraz z danymi
    public String zwrocKategorieIDane(Element e2) {
        String data = "";
        String categoryAndData = null;
        String[] temp = e2.toString().split("span>");

        if (temp.length > 1) {
            int o = temp[1].indexOf("<");
            categoryAndData = temp[1].substring(0, o);
        }

        // dane 
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

    // tworzy obiekt z tymi danymi 
    public void stworzFilm(List<String> mul, String nazwa, String link, String img, String opis, String opis2, String tagi) throws IOException, IllegalArgumentException, IllegalAccessException {

        Film film = new Film();
        Document d2 = Jsoup.connect(link).get();

        int index = nazwa.indexOf("|");
        if (index != -1) {
            nazwa = nazwa.substring(0, index);
        }

        for (int j = 0; j < mul.size(); j++) {
            if (mul.get(j) != null) {
                String part = mul.get(j);
                if (part != null) {
                    int pole = part.indexOf(":");

                    if (pole != -1 && pole + 1 < part.length()) {

                        switch (part.substring(0, pole)) {
                            case "czas trwania":
                                film.setCzasTrwania(part.substring(pole + 2, part.length()));
                                break;
                            case "lektor":
                                film.setLektor(part.substring(pole + 2, part.length()));
                                break;
                            case "kategoria":
                                film.setKategoria(part.substring(pole + 2, part.length()));
                                break;
                            case "gatunek":
                                film.setGatunek(part.substring(pole + 2, part.length()));
                                break;
                            case "linkPriv":
                                film.setLink(part.substring(pole + 2, part.length()));
                                break;
                            case "język":
                                film.setJęzyk(part.substring(pole + 2, part.length()));
                                break;
                            case "reżyseria":
                                film.setReżyseria(part.substring(pole + 2, part.length()));
                                break;
                            case "scenariusz":
                                film.setScenariusz(part.substring(pole + 2, part.length()));
                                break;
                            case "rok produkcji":
                                film.setRokProdukcji(part.substring(pole + 2, part.length()));
                                break;
                            case "producent":
                                film.setProducent(part.substring(pole + 2, part.length()));
                                break;
                            case "instrumentalista":
                                film.setInstrumentalista(part.substring(pole + 2, part.length()));
                                break;
                            case "charakteryzacja":
                                film.setCharakteryzacja(part.substring(pole + 2, part.length()));
                                break;
                            case "prowadzący":
                                film.setProwadzący(part.substring(pole + 2, part.length()));
                                break;
                            case "barwa":
                                film.setBarwa(part.substring(pole + 2, part.length()));
                                break;
                            case "jakość":
                                film.setJakość(part.substring(pole + 2, part.length()));
                                break;
                            case "kategoria wiekowa":
                                if (pole + 2 >= part.length()) {
                                } else {
                                    film.setKategoriaWiekowa(part.substring(pole + 2, part.length()));
                                }
                                break;
                            case "uczestnik":
                                film.setUczestnik(part.substring(pole + 2, part.length()));
                                break;
                            case "aktor":
                                film.setAktor(part.substring(pole + 2, part.length()));
                                break;
                            case "zdjęcia":
                                film.setZdjęcia(part.substring(pole + 2, part.length()));
                                break;
                            case "realizacja":
                                film.setRealizacja(part.substring(pole + 2, part.length()));
                                break;
                            case "montaż":
                                film.setMontaż(part.substring(pole + 2, part.length()));
                                break;
                            case "scenografia":
                                film.setScenografia(part.substring(pole + 2, part.length()));
                                break;
                        }
                    }
                }
            }
        }

        film.setNazwa(nazwa);
        film.setObraz(img);
        film.setLink(link);
        film.setOpis(opis);
        film.setOpis2(opis2);
        film.setTagi(tagi);

        for (Field fff : film.getClass().getDeclaredFields()) {
            fff.setAccessible(true);
            if (fff.get(film) == null) {
                fff.set(film, "");
            }
        }

        filmList.add(film);
    }

    public void ustawIloscStron(String adrStrony) throws IOException {
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

    // pobiera z jednej storny dane i linki
    public void odczytStronIPodstron(String adrStrony) throws IllegalArgumentException, IllegalAccessException, IOException {

        String link;
        String img = "", tag = "";
        String opis = "", opis2 = "";
        String prefix = "http://ninateka.pl";

        List<String> categoriesAndData = new ArrayList<>();

        try {
            Document d = Jsoup.connect(adrStrony).get();
            int[] nrStron = new int[20];

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

                    // kategorie
                    for (Element e2 : content2.select("#tabs-1 li")) {
                        String pom = zwrocKategorieIDane(e2);
                        if (pom != null) {
                            categoriesAndData.add(zwrocKategorieIDane(e2));
                        }
                    }

                    // druga zakladka
                    for (Element e2 : content2.select("#tabs-2 li")) {
                        String secondTab = zwrocKategorieIDane(e2);
                        if (secondTab != null) {
                            categoriesAndData.add(secondTab);
                        }
                    }

                    // opisy 
                    for (Element e2 : content2.select(".rawdescription.read-ivona")) {
                        opis = e2.text();
                    }

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

                    //opis dodatkowy filmu
                    Elements content3 = d2.select(".movie-description-text");
                    opis2 = content3.select("div").text();

                    //System.out.println("nie było :D ");
                    stworzFilm(categoriesAndData, nazwa, link, img, opis, opis2, tag);

                    categoriesAndData.clear();
                    nazwa = "";
                    link = "";
                    img = "";
                    opis = "";
                    opis2 = "";
                    tag = "";
                } else {
                    //System.out.println("to juz było :(");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void zmianaAdresow() {
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
