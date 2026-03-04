import org.json.JSONArray;
import org.json.JSONObject;
import Component.Email;
import Servisofts.SConfig;

public class TestEmail {
    public static void main(String[] args) {
        try {

            SConfig.getJSON();
            // Destinatarios
            JSONArray mailTo = new JSONArray();
            mailTo.put("ruddypazd@gmail.com"); // Cambia esto por tu email de prueba
            
            // Datos del correo
            JSONObject data = new JSONObject();
            data.put("subject", "Prueba de envío - Staff Pro USA");
            data.put("path", "mail/registro_exitoso.html"); // O "mail/recuperar_pass.html"
            
            // Parámetros para reemplazar en la plantilla
            JSONObject params = new JSONObject();
            params.put("nombre", "Usuario de Prueba");
            params.put("email", "test@example.com");
            params.put("codigo", "123456");
            params.put("link", "https://staffprousa.com/verificar");
            
            // Enviar correo
            System.out.println("Enviando correo de prueba...");
            new Email(mailTo, data, params);
            
            System.out.println("Proceso iniciado. Revisa la consola para ver el resultado del envío.");
            
            // Esperar un poco para que el thread termine
            Thread.sleep(5000);
            
        } catch (Exception e) {
            System.err.println("Error al enviar correo de prueba:");
            e.printStackTrace();
        }
    }
}
