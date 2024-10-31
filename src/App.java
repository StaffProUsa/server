import Component.Background;
import Controllers.BancoGanaderoController;
import Controllers.Pub;
import Servisofts.Servisofts;
import Servisofts.http.Rest;
public class App {
    
    public static void main(String[] args) {
        try {
            Servisofts.DEBUG = false;
            Servisofts.ManejadorCliente = ManejadorCliente::onMessage;
            Servisofts.Manejador = Manejador::onMessage;
            Servisofts.initialize();
            Rest.addController(BancoGanaderoController.class);
            Rest.addController(Pub.class);
            //new Background().start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
