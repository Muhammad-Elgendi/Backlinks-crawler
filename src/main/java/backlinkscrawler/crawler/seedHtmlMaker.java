package backlinkscrawler.crawler;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class seedHtmlMaker {

    public static void main(String [] args) throws FileNotFoundException {
        /**
         * Read seeds from topSites.csv file
         */

        Scanner scanner = new Scanner(new File("./topSites.csv"));
        while (scanner.hasNextLine()) {
            Thread thread = new SeedConnector("http://"+scanner.nextLine());

            thread.start();
        }

    }

    /**
     * Doesn't redirect from http to https
     */
//    public static String getRedirectUrl(String url) {
//        URL urlTmp = null;
//        String redUrl = null;
//        HttpURLConnection connection = null;
//
//        try {
//            urlTmp = new URL(url);
//        } catch (MalformedURLException e1) {
//            e1.printStackTrace();
//        }
//
//        boolean redirect = true;
//        while(redirect) {
//            try {
//                connection = (HttpURLConnection) urlTmp.openConnection();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                if(connection.getResponseCode() == 200){
//                    redirect =false;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        redUrl = connection.getURL().toString();
//        connection.disconnect();
//
//        return redUrl;
//    }
}

class SeedConnector extends Thread {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SeedConnector.class);
    private String url;
    public SeedConnector(String url) {
        this.url = url;
    }

    public void run() {

        try {
            logger.info(getRedirectUrl(this.url));
        } catch (IOException e) {
            logger.error("Error with "+this.url);
        } catch (URISyntaxException e) {
            logger.error("Error with "+this.url);
        }
    }

    public String getRedirectUrl(String url) throws IOException, URISyntaxException {

        CloseableHttpClient httpclient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();

//        HttpClient httpclient = HttpClientBuilder.create()
//                .setRedirectStrategy(new LaxRedirectStrategy()).build();
        try {
            HttpClientContext context = HttpClientContext.create();
            HttpGet httpGet = new HttpGet(url);
//            System.out.println("Executing request " + httpGet.getRequestLine());
//            System.out.println("----------------------------------------");

            httpclient.execute(httpGet, context);
            HttpHost target = context.getTargetHost();
            List<URI> redirectLocations = context.getRedirectLocations();
            URI location = URIUtils.resolve(httpGet.getURI(), target, redirectLocations);
//            System.out.println("Final HTTP location: " + location.toASCIIString());
            return location.toASCIIString();
        } finally {
            httpclient.close();
        }
    }
}

