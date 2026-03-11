package Component;

import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SPGConect;

public class Wtspp {

    public static void sendInvitacionEvento(String telefono_, JSONObject data) {
        Thread hilo = new Thread(() -> {
            try {
                System.out.println("El hilo está en ejecución.");

                JSONObject _data = data;
                JSONObject companyCompleta = SPGConect.ejecutarConsultaObject(
                        "select companias_get_by_key_staff('" + data.getString("key_staff") + "') as json");

                String msg = "*" + companyCompleta.getJSONObject("cliente").getString("descripcion")
                        + "*\n\nInvites you to be part of a event *"
                        + companyCompleta.getJSONObject("evento").getString("descripcion") + "*\n\n";
                msg += "\n\n";
                msg += companyCompleta.getJSONObject("evento").optString("observacion");
                msg += "\n\n";
                msg += "¡Click the link and accept the invitation!\n\n\n" +
                        "https://staffpro-usa.com/link/invitation_event?key=" + data.optString("key", "") + " \n\n\n"
                        + companyCompleta.getString("descripcion");

                String telefono = telefono_.replaceAll("\\+", "").replaceAll(" ", "");
                JSONObject send_ = new JSONObject();
                send_.put("key", SConfig.getJSON("wtsp").getString("key"));
                send_.put("mensaje", msg);
                // send_.put("imagen", base64String);
                send_.put("numero", telefono);
                send_.put("key_usuario", _data.getString("key_usuario"));
                // send_ = fetch.post(SConfig.getJSON("wtsp").getString("url") + "/send/",
                // send_);
                send_ = fetch.post("https://n8n.servisofts.com/webhook/72805880-f580-45d1-bcff-8a2dc94aebef", send_);
                // fetch.get( );
                System.out.println(send_);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        hilo.start();
    }
}
