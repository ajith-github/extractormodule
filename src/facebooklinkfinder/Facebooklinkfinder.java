/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facebooklinkfinder;


//imports in the program

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.log4j.BasicConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author ajith
 */
public class Facebooklinkfinder {
    
    
    
    private static final Logger log = Logger.getLogger(Facebooklinkfinder.class);
    private OAuthConsumer consumer = null;
    private String responseBody = "";

    protected static String yahooServer = "http://yboss.yahooapis.com/ysearch/";


    // Please provide your consumer key here
    private static String consumer_key = "dj0yJmk9RFFwYVpaeXJwa29IJmQ9WVdrOU4zWkROa3haTXpBbWNHbzlNVGM1TURBeU1EazJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0xOA--";

    // Please provide your consumer secret here
    private static String consumer_secret = "bf6cca46d109013c0a0c0da0050a44651936da0b";
    /** Encode Format */
    private static final String ENCODE_FORMAT = "UTF-8";

    /** Call Type */
    private static final String callType = "web";

    private static  String replaceurl(String content) {

           content= content.replace("/","%2F");
           content = content.replace(":", "%3A");
            
           return content;
        }

    
        public int returnHttpData(String query) throws UnsupportedEncodingException, Exception{
            int status = 0;
            
            String newquery;
            String params = callType;
            newquery=replaceurl(query);
            params = params.concat("?q=" + newquery+"&count=3&sites=facebook.com%2Ctwitter.com");

            String url = yahooServer + params;

            OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key, consumer_secret);
            setOAuthConsumer(consumer);

            URLDecoder.decode(url, ENCODE_FORMAT);
            System.out.print(query + ", ");
            int responseCode = sendGetRequest(url);
            
            homepagelinks(query);
            return status;

        }

        public int sendGetRequest(String url) throws IOException,OAuthMessageSignerException,OAuthExpectationFailedException,OAuthCommunicationException {

             
             int responseCode = 500;
            
             try {
                    HttpURLConnection uc = getConnection(url);

                    responseCode = uc.getResponseCode();

                if(200 == responseCode || 401 == responseCode || 404 == responseCode){
                    BufferedReader rd = new BufferedReader(new InputStreamReader(responseCode==200?uc.getInputStream():uc.getErrorStream()));
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);

                    }

                String response = sb.toString();
                try{
                        
                       JSONObject json = new JSONObject(response);

                       JSONArray ja = json.getJSONObject("bossresponse").getJSONObject("web").getJSONArray("results");

                       String str = "";

                       for (int i = 0; i < ja.length(); i++) {
                         JSONObject j = ja.getJSONObject(i);
                         str = j.getString("url");
                         System.out.print(str); 
                         if (i < 2)
                             System.out.print(", ");
                        }

              }catch (Exception e) {
                        System.err.println("Something went wrong...");
                        e.printStackTrace();
              }       
             
              rd.close();
              setResponseBody(sb.toString());
         }
        } catch (MalformedURLException ex) {
               throw new IOException( url + " is not valid");
      } catch (IOException ie) {
             throw new IOException("IO Exception " + ie.getMessage());
      }

        return responseCode;
   }

     public HttpURLConnection getConnection(String url) throws IOException, OAuthMessageSignerException,OAuthExpectationFailedException, OAuthCommunicationException{
         try {
                 URL u = new URL(url);

                 HttpURLConnection uc = (HttpURLConnection) u.openConnection();

                 if (consumer != null) {
                     try {
                         consumer.sign(uc);

                     } catch (OAuthMessageSignerException e) {
                         throw e;

                     } catch (OAuthExpectationFailedException e) {
                     throw e;

                     } catch (OAuthCommunicationException e) {
                     throw e;
                     }
                     uc.connect();
                 }
                 return uc;
         } catch (IOException e) {
         log.error("Error signing the consumer", e);
         throw e;
         }
        }

        public void setOAuthConsumer(OAuthConsumer consumer) {
            this.consumer = consumer;
        }
        private static void print(String msg, Object... args) {
            System.out.println(String.format(msg, args));
        }

        private static String trim(String s, int width) {
            if (s.length() > width)
                return s.substring(0, width-1) + ".";
            else
                return s;
        }

         public String getResponseBody() {
            return responseBody;
        }

        public void setResponseBody(String responseBody) {
            if (null != responseBody) {

                this.responseBody = responseBody;
            }
        }

        public void homepagelinks(String url1)throws UnsupportedEncodingException, Exception{



            Document doc = Jsoup.connect(url1).get();
            Elements links = doc.select("a[href]");   
            Document doc1 = Jsoup.parse(url1);


            for (Element link : links) {
                String linkabshref = link.attr("abs:href");

                if ( linkabshref.contains("www.facebook.com/") || linkabshref.contains("www.twitter.com/") ){
                    System.out.print(", " +linkabshref);
               }
            }
            return;
        }


         public static void main(String[] args) throws IOException, UnsupportedEncodingException {

            BasicConfigurator.configure();
            try{
                String content = "http://www.logica.com";
                Facebooklinkfinder facebooklinkfinder = new Facebooklinkfinder();
                facebooklinkfinder.returnHttpData(content);

            }catch(Exception e)
            {
                System.out.println("something went wrong.........");
                System.out.println(e);
            }
        }
    }

    