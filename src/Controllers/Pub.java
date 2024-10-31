package Controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import Component.Actividad;
import Component.Entrada;
import Component.Evento;
import Component.SolicitudQr;
import Component.TipoEntrada;
import Servisofts.SConsole;
import Servisofts.http.Status;
import Servisofts.http.Exception.*;
import Servisofts.http.annotation.*;

@RestController
@RequestMapping("link")
public class Pub {

    @GetMapping("/evento")
    public String getHtml(@RequestParam("key") String key, HttpExchange exchange) throws HttpException {

        try {

            if (key == null) {
                throw new HttpException(Status.BAD_REQUEST, "Missing 'key' parameter");
            }

            String url = "https://casagrande.servisofts.com";
            String url2 = url+"/evento?key="+key;

            if(this.redirect(exchange, url2)){
                return "";
            }
            
            JSONObject evento = Evento.getByKey(key);
            JSONObject actividad = Actividad.getByKeyEvento(key);

            String title = evento.getString("descripcion");
            String descripcion = evento.getString("observacion");
            String image = url+"/repo/CasaGrande/actividad/"+JSONObject.getNames(actividad)[0];

            

            return "<!doctype html>\n" + //
                                "<html lang=\"es\">\n" + //
                                "\n" + //
                                "<head>\n" + //
                                "    <meta charset=\"utf-8\" />\n" + //
                                "    <meta name=\"viewport\" content=\"width=device-width,user-scalable=no,minimum-scale=1,maximum-scale=1\">\n" + //
                                "    <meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate\">\n" + //
                                "    <meta http-equiv=\"Pragma\" content=\"no-cache\">\n" + //
                                "    <meta http-equiv=\"Expires\" content=\"0\">\n" + //
                                "\n" + //
                                "    <meta property=\"og:url\" content=\""+url+"/link/evento?key="+key+"\">\n" + //
                                "    <meta property=\"og:title\" content=\""+title+"\">\n" + //
                                "    <meta property=\"og:type\" content=\"website\">\n" + //
                                
                                "<meta property='al:android:url content='https://casagrande.servisofts.com'> \n"+
                                "<meta property='al:android:app_name content='Casa Grande'> \n"+
                                "<meta property='al:android:package content='com.casagrande_app'> \n"+

                                "    <meta property=\"og:image\" content=\""+image+"\">\n" + //
                                "    <meta property=\"og:image:width\" content=\"512\">\n" + //
                                "    <meta property=\"og:image:height\" content=\"512\">\n" + //


                                "    <meta property=\"og:description\" content=\""+descripcion+"\">\n" + //
                                "    <meta property=\"og:site_name\" content=\"Nombre del Sitio\">\n" + //
                                "    <meta property=\"og:locale\" content=\"es_ES\">\n" + //
                                "\n" + //
                                "\n" + //
                                "    <link rel=\"icon\" href=\"https://casagrande.servisofts.com/favicon.ico\" />\n" + //
                                "    <meta name=\"description\" content=\""+descripcion+"\" />\n" + //
                                "    <link rel=\"apple-touch-icon\" href=\""+image+"\" />\n" + //
                                "    <title>"+title+"</title>\n" + //
                                
                                "</head>\n" + //
                                "\n" + //
                                "<body><noscript>You need to enable JavaScript to run this app.</noscript>\n" + //
                                "    <h1 id=\"root\">hola mundo</h1>\n" + //
                                "</body>\n" + //
                                "\n" + //
                                "</html>";
        } catch (Exception e) {
            SConsole.error("Error ", e.getMessage());
            throw new HttpException(Status.BAD_REQUEST, e.getLocalizedMessage());
        }

        // DOC-> Tengo que retornar el status 200;
        // return "exito";
    }

    @GetMapping("/manilla")
    public String getHtmlManilla(@RequestParam("key") String key, @RequestParam("key_ui") String key_ui, HttpExchange exchange) throws HttpException {

        try {
            
            
            if (key == null) {
                throw new HttpException(Status.BAD_REQUEST, "Missing 'key' parameter");
            }


            if (key_ui == null) {
                throw new HttpException(Status.BAD_REQUEST, "Missing 'key' parameter");
            }

            String url = "https://casagrande.servisofts.com";

            String url2 = url+"/manilla?key="+key+"&key_ui="+key_ui;

            
            if(this.redirect(exchange, url2)){
                return "";
            }

            JSONObject entrada  = Entrada.getByKey(key);
            JSONObject tipoEntrada = TipoEntrada.getByKey(entrada.getString("key_tipo_entrada"));

            JSONObject evento = Evento.getByKey(tipoEntrada.getString("key_evento"));
            JSONObject actividad = Actividad.getByKeyEvento(tipoEntrada.getString("key_evento"));

            
          //  response.sendRedirect(url+"/evento?key"+key);

            String title = "Entrada para "+evento.getString("descripcion");
            String descripcion = "Entrada "+tipoEntrada.getString("descripcion")+" #"+entrada.get("numero");
            String image = url+"/repo/CasaGrande/actividad/"+JSONObject.getNames(actividad)[0];

            

            return "<!doctype html>\n" + //
                                "<html lang=\"es\">\n" + //
                                "\n" + //
                                "<head>\n" + //
                                "    <meta charset=\"utf-8\" />\n" + //
                                "    <meta name=\"viewport\" content=\"width=device-width,user-scalable=no,minimum-scale=1,maximum-scale=1\">\n" + //
                                "    <meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate\">\n" + //
                                "    <meta http-equiv=\"Pragma\" content=\"no-cache\">\n" + //
                                "    <meta http-equiv=\"Expires\" content=\"0\">\n" + //
                                "\n" + //
                                "    <meta property=\"og:url\" content=\""+url+"/link/manilla?key="+key+"&key_ui="+key_ui+"\">\n" + //
                                "    <meta property=\"og:title\" content=\""+title+"\">\n" + //
                                "    <meta property=\"og:type\" content=\"website\">\n" + //
                                
                                "<meta property='al:android:url content='https://casagrande.servisofts.com'> \n"+
                                "<meta property='al:android:app_name content='Casa Grande'> \n"+
                                "<meta property='al:android:package content='com.casagrande_app'> \n"+

                                "    <meta property=\"og:image\" content=\""+image+"\">\n" + //
                                "    <meta property=\"og:image:width\" content=\"512\">\n" + //
                                "    <meta property=\"og:image:height\" content=\"512\">\n" + //


                                "    <meta property=\"og:description\" content=\""+descripcion+"\">\n" + //
                                "    <meta property=\"og:site_name\" content=\"Nombre del Sitio\">\n" + //
                                "    <meta property=\"og:locale\" content=\"es_ES\">\n" + //
                                "\n" + //
                                "\n" + //
                                "    <link rel=\"icon\" href=\"https://casagrande.servisofts.com/favicon.ico\" />\n" + //
                                "    <meta name=\"description\" content=\""+descripcion+"\" />\n" + //
                                "    <link rel=\"apple-touch-icon\" href=\""+image+"\" />\n" + //
                                "    <title>"+title+"</title>\n" + //
                                
                                "</head>\n" + //
                                "\n" + //
                                "<body><noscript>You need to enable JavaScript to run this app.</noscript>\n" + //
                                "    <h1 id=\"root\">hola mundo</h1>\n" + //
                                "</body>\n" + //
                                "\n" + //
                                "</html>";
        } catch (Exception e) {
            SConsole.error("Error ", e.getMessage());
            throw new HttpException(Status.BAD_REQUEST, e.getLocalizedMessage());
        }

        // DOC-> Tengo que retornar el status 200;
        // return "exito";
    }

    public boolean redirect(HttpExchange exchange, String url) throws IOException{

        String userAgent = exchange.getRequestHeaders().getFirst("User-Agent");
        System.out.println(userAgent);

        if(!userAgent.contains("facebookexternalhit") && 
        !userAgent.contains("WhatsApp") && 
        !userAgent.contains("LinkedInBot") && 
        !userAgent.contains("Twitterbot") && 
        !userAgent.contains("Discordbot") && 
        !userAgent.contains("TelegramBot")){
            
            if(userAgent.contains("Android")){
                exchange.getResponseHeaders().set("Location", "https://play.google.com/store/apps/details?id=com.casagrande_app");
                exchange.sendResponseHeaders(302, -1);
                
                return true;
            }

            if(userAgent.contains("AppleWebKit") && userAgent.contains("Mobile")){
                exchange.getResponseHeaders().set("Location", "https://apps.apple.com/us/app/casa-grande/id6443572957");
                exchange.sendResponseHeaders(302, -1);
                return true;
            }

            exchange.getResponseHeaders().set("Location", url);
            exchange.sendResponseHeaders(302, -1);
            return true;
        }
        return false;
    }

}