package Component;

import org.json.JSONArray;
import org.json.JSONObject;

import Server.SSSAbstract.SSServerAbstract;
import Servisofts.SConsole;
import Servisofts.SPGConect;

public class Background extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                
                /*String consulta = "SELECT _reservas() as json";
                JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
                if (data.length() > 0) {
                    System.out.println(data);
                    JSONObject send = new JSONObject();
                    send.put("component", "mesa");
                    send.put("type", "editarAll");
                    send.put("data", data);
                    send.put("estado", "borrar");
                    SSServerAbstract.sendAllServer(send.toString());
                }


                */
                Thread.sleep(5000);
            } catch (Exception e) {
                SConsole.error(e.getLocalizedMessage());
            }
        }
    }

    
}
