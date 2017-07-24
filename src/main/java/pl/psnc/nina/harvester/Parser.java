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

    private List listaFilmow = new ArrayList(); //nowe odczytane filmy
    private Map<String, Film> mapaFilmow = new LinkedHashMap<>();

    public Parser() throws IOException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InterruptedException, InvalidFormatException {
        odczyt();
    }

    public void odczyt() throws IOException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InterruptedException, InvalidFormatException {

        ustawIloscStron("http://ninateka.pl/filmy?page=1");
        System.out.println("ilosc stron: " + numberOfPages);
        zmianaAdresow();

        excel e = new excel();
        mapaFilmow = e.readCsv();

        odczytajDaneIZapisz(this, e);

    }

    public void odczytajDaneIZapisz(Parser t, excel e) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException {

        long start = System.currentTimeMillis();
       
        
        // !!!!!!!!
        for (int i = 0; i < 4; i++) {

            long start1 = System.currentTimeMillis();
            System.out.println("i: " + i);
            t.odczytStronIPodstron(webPage.get(i));
            e.writeCsv(listaFilmow);

            listaFilmow.clear();
            long end1 = System.currentTimeMillis();
        }
        long end = System.currentTimeMillis();

        System.out.println("czas calkowity: " + (end - start));
        System.out.println("koniec odczytu");
    }

    // zwraca jedna kategorie wraz z danymi
    public String zwrocKategorieIDane(Element e2) {
        String dane = "";
        String kategoriaDane = null;
        String[] temp = e2.toString().split("span>");

        if (temp.length > 1) {
            int o = temp[1].indexOf("<");
            kategoriaDane = temp[1].substring(0, o);
        }

        // dane 
        if (temp.length > 2) {
            if (temp[2].length() > 7) {
                String pra = temp[2].substring(0, 8);

                if (pra.equals(" <a href")) {

                    String html = temp[2];
                    String test;

                    Document doc = Jsoup.parse(html);
                    Elements content3 = doc.select("a");

                    for (Element e3 : content3) {
                        Node dane1 = e3.unwrap();
                        String daneD = dane1.toString();
                        if (daneD.length() >= 2) {
                            if (daneD.substring(0, 2).contains("\n")) {
                                daneD = daneD.substring(1, daneD.length());
                            }
                        }
                        dane = dane + "," + daneD;
                    }
                } else {
                    int o = temp[2].indexOf("<");

                    if (o != -1 && !temp[2].equals("<br></li>")) {
                        dane = dane + "," + temp[2].substring(1, o);
                    }
                }
            }
            kategoriaDane = kategoriaDane + dane;
        }
        return kategoriaDane;
    }

    // tworzy obiekt z tymi danymi 
    public void stworzFilm(List<String> mul, String nazwa, String link, String img, String opis, String opis2, String tagi) throws IOException, IllegalArgumentException, IllegalAccessException {

        Film film = new Film();
        Document d2 = Jsoup.connect(link).get();

        int pozycja = nazwa.indexOf("|");
        if (pozycja != -1) {
            nazwa = nazwa.substring(0, pozycja);
        }

        for (int j = 0; j < mul.size(); j++) {
            if (mul.get(j) != null) {
                String czesc = mul.get(j);
                if (czesc != null) {
                    int pole = czesc.indexOf(":");

                    if (pole != -1 && pole + 1 < czesc.length()) {

                        switch (czesc.substring(0, pole)) {
                            case "czas trwania":
                                film.setCzasTrwania(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "lektor":
                                film.setLektor(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "kategoria":
                                film.setKategoria(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "gatunek":
                                film.setGatunek(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "linkPriv":
                                film.setLink(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "język":
                                film.setJęzyk(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "reżyseria":
                                film.setReżyseria(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "scenariusz":
                                film.setScenariusz(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "rok produkcji":
                                film.setRokProdukcji(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "producent":
                                film.setProducent(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "instrumentalista":
                                film.setInstrumentalista(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "charakteryzacja":
                                film.setCharakteryzacja(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "prowadzący":
                                film.setProwadzący(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "barwa":
                                film.setBarwa(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "jakość":
                                film.setJakość(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "kategoria wiekowa":
                                if (pole + 2 >= czesc.length()) {
                                } else {
                                    film.setKategoriaWiekowa(czesc.substring(pole + 2, czesc.length()));
                                }
                                break;
                            case "uczestnik":
                                film.setUczestnik(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "aktor":
                                film.setAktor(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "zdjęcia":
                                film.setZdjęcia(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "realizacja":
                                film.setRealizacja(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "montaż":
                                film.setMontaż(czesc.substring(pole + 2, czesc.length()));
                                break;
                            case "scenografia":
                                film.setScenografia(czesc.substring(pole + 2, czesc.length()));
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

//        if (mapaFilmow.containsKey(film.getLink())) {
//            System.out.println("BYŁOOOOOOOOOOOOOOOO");
//        } else {
        listaFilmow.add(film);
////        }

    }

    public void ustawIloscStron(String adrStrony) throws IOException {
        Document d = Jsoup.connect(adrStrony).get();
        int[] nrStron = new int[20];

        //odczyt ilosci stron 
        Elements iloscStronElem = d.select(".span6.pagination");
        int i = 0;

        for (Element e : iloscStronElem.select("a")) {
            String ostatnia = e.toString();
            ostatnia = ostatnia.substring(21, ostatnia.length());
            if (ostatnia.indexOf("\"") != -1) {
                String[] c = ostatnia.split("\"");
                c = c[0].split(" ");
                ostatnia = c[0];
            }
            nrStron[i] = Integer.parseInt(ostatnia);
            i++;
        }
        for (int n : nrStron) {
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

        List<String> kategorieIDane = new ArrayList<>();

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
                if (!mapaFilmow.containsKey(link)) {

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
                            kategorieIDane.add(zwrocKategorieIDane(e2));
                        }
                    }

                    // druga zakladka
                    for (Element e2 : content2.select("#tabs-2 li")) {
                        String druga = zwrocKategorieIDane(e2);
                        if (druga != null) {
                            kategorieIDane.add(druga);
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
                    stworzFilm(kategorieIDane, nazwa, link, img, opis, opis2, tag);

                    kategorieIDane.clear();
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
        List<String> lista = new ArrayList<String>();

        for (int i = 0; i < numberOfPages; i++) {
            url1 = adr[0].concat("" + (i + 1));
            lista.add(url1);
        }
        webPage = lista;
    }
}
