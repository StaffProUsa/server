package Component;

import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SPGConect;
import Servisofts.SUtil;

// Push automáticas de StaffPro (web) vía el microservicio notification (sendV2 con tags).
// Cada token recibe el idioma que el usuario eligió en la app (tag "language" = en | es);
// los tokens sin ese tag (registrados antes) reciben el texto bilingüe "EN / ES".
// Se apagan con "push": {"enabled": false} en config.json.
public class Push {
    public static final String URL = "https://staffpro-usa.com";

    public static boolean enabled() {
        try {
            return SConfig.getJSON("push").optBoolean("enabled", true);
        } catch (Exception e) {
            return true;
        }
    }

    public static void aUsuario(String key_usuario, String key_emisor, String tituloEn, String tituloEs,
            String cuerpoEn, String cuerpoEs, String path) {
        if (!enabled() || vacio(key_usuario)) return;
        // Nunca notificar a quien hizo la acción
        if (key_usuario.equals(key_emisor)) return;
        try {
            enviar(key_emisor, key_usuario, "en", tituloEn, cuerpoEn, path);
            enviar(key_emisor, key_usuario, "es", tituloEs, cuerpoEs, path);
            // "is null" = tokens sin tag language (el microservicio lo traduce a COALESCE(tags->>'language','') = '')
            enviar(key_emisor, key_usuario, "is null", tituloEn + " / " + tituloEs, cuerpoEn + "\n" + cuerpoEs, path);
            System.out.println("[push] " + tituloEn + " -> " + key_usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviar(String key_emisor, String key_usuario, String language, String titulo, String cuerpo,
            String path) throws Exception {
        JSONObject tags = new JSONObject().put("key_usuario", key_usuario).put("language", language);
        new Notification().send_urlV2Tags(key_emisor, tags, titulo, cuerpo, URL + path);
    }

    // Datos para los textos: puesto, evento y empresa de un staff
    public static JSONObject contextoStaff(String key_staff) {
        try {
            JSONObject ctx = SPGConect.ejecutarConsultaObject("select to_json(sq.*) as json from (\n" +
                    "  select staff.key as key_staff, evento.key as key_evento, evento.descripcion as evento,\n" +
                    "         staff_tipo.descripcion as puesto, company.descripcion as company\n" +
                    "  from staff\n" +
                    "    join evento on evento.key = staff.key_evento\n" +
                    "    left join staff_tipo on staff_tipo.key = staff.key_staff_tipo\n" +
                    "    left join company on company.key = evento.key_company\n" +
                    "  where staff.key = '" + q(key_staff) + "'\n" +
                    ") sq");
            return ctx != null ? ctx : new JSONObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static String puestoEn(JSONObject ctx) {
        return ctx.optString("puesto", "Staff") + " at " + ctx.optString("evento", "your event");
    }

    public static String puestoEs(JSONObject ctx) {
        return ctx.optString("puesto", "Staff") + " en " + ctx.optString("evento", "tu evento");
    }

    // #1 Invitación a un evento -> trabajador
    public static void invitacionEvento(String key_staff_usuario, String key_staff, String key_trabajador, String key_emisor) {
        JSONObject ctx = contextoStaff(key_staff);
        aUsuario(key_trabajador, key_emisor,
                "New job invitation", "Nueva invitación de trabajo",
                puestoEn(ctx) + ". Tap to accept.",
                puestoEs(ctx) + ". Toca para aceptar.",
                "/invitationDetail?key=" + key_staff_usuario);
    }

    // #2 Aceptó la invitación -> admin que invitó y jefe del puesto
    public static void invitacionAceptada(JSONObject staffUsuario, String key_emisor) {
        JSONObject ctx = contextoStaff(staffUsuario.optString("key_staff"));
        String trabajador = staffUsuario.optString("key_usuario");
        String nombre = nombre(trabajador);
        for (String destino : destinosAdmin(staffUsuario)) {
            aUsuario(destino, vacio(key_emisor) ? trabajador : key_emisor,
                    "Invitation accepted", "Invitación aceptada",
                    nombre + " confirmed " + puestoEn(ctx) + ".",
                    nombre + " confirmó " + puestoEs(ctx) + ".",
                    "/company/event?key_evento=" + ctx.optString("key_evento"));
        }
    }

    // #3 Rechazó la invitación ("No, gracias") -> admin que invitó
    public static void invitacionRechazada(JSONObject staffUsuario, String key_emisor) {
        JSONObject ctx = contextoStaff(staffUsuario.optString("key_staff"));
        String trabajador = staffUsuario.optString("key_usuario");
        String nombre = nombre(trabajador);
        aUsuario(staffUsuario.optString("key_usuario_aprueba"), vacio(key_emisor) ? trabajador : key_emisor,
                "Invitation declined", "Invitación rechazada",
                nombre + " is not available for " + puestoEn(ctx) + ".",
                nombre + " no está disponible para " + puestoEs(ctx) + ".",
                "/company/event?key_evento=" + ctx.optString("key_evento"));
    }

    // #4 Desinvitado -> trabajador
    public static void invitacionCancelada(String key_staff, String key_trabajador, String key_emisor) {
        JSONObject ctx = contextoStaff(key_staff);
        aUsuario(key_trabajador, key_emisor,
                "Invitation cancelled", "Invitación cancelada",
                "Your invitation for " + puestoEn(ctx) + " was cancelled.",
                "Tu invitación para " + puestoEs(ctx) + " fue cancelada.",
                "/inicio");
    }

    // Invitación a una empresa (un admin agrega al usuario) -> usuario
    public static void agregadoAEmpresa(String key_company, String key_usuario, String key_emisor) {
        String company = "A company";
        try {
            JSONObject c = SPGConect.ejecutarConsultaObject(
                    "select to_json(company.*) as json from company where company.key = '" + q(key_company) + "'");
            if (c != null && !vacio(c.optString("descripcion"))) company = c.optString("descripcion");
        } catch (Exception e) {
            e.printStackTrace();
        }
        aUsuario(key_usuario, key_emisor,
                "Company invitation", "Invitación de empresa",
                company + " invited you to join its team.",
                company + " te invitó a unirte a su equipo.",
                "/my_companys");
    }

    // Admin que invitó y jefe del puesto, sin repetir
    public static JSONArray destinosAdminArray(JSONObject staffUsuario) {
        JSONArray out = new JSONArray();
        String aprueba = staffUsuario.optString("key_usuario_aprueba");
        String jefe = staffUsuario.optString("key_usuario_atiende");
        if (!vacio(aprueba)) out.put(aprueba);
        if (!vacio(jefe) && !jefe.equals(aprueba)) out.put(jefe);
        return out;
    }

    public static String[] destinosAdmin(JSONObject staffUsuario) {
        JSONArray a = destinosAdminArray(staffUsuario);
        String[] out = new String[a.length()];
        for (int i = 0; i < a.length(); i++) out[i] = a.getString(i);
        return out;
    }

    public static String nombre(String key_usuario) {
        String n = usuario.getNombreUsuario(key_usuario);
        return vacio(n) ? "A worker" : n;
    }

    // Registro para no repetir avisos programados
    public static void registrar(String key_usuario, String tipo, String ref_key) {
        try {
            SPGConect.ejecutarUpdate("insert into push_log (key, key_usuario, tipo, ref_key, fecha_on) values ('"
                    + SUtil.uuid() + "', '" + q(key_usuario) + "', '" + q(tipo) + "', '" + q(ref_key)
                    + "', now()) on conflict do nothing");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean vacio(String s) {
        return s == null || s.isEmpty() || s.equals("null");
    }

    public static String q(String s) {
        return s == null ? "" : s.replace("'", "''");
    }
}
