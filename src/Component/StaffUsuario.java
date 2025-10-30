package Component;

import java.net.URL;
import java.util.Base64;

import org.bouncycastle.util.encoders.UrlBase64;
import org.eclipse.jetty.util.UrlEncoded;
import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.Server.SSSAbstract.SSServerAbstract;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class StaffUsuario {
    public static final String COMPONENT = "staff_usuario";

    // key_usuario_atiende es el jefe

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "getByKey":
                getByKey(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "aprobar":
                aprobar(obj, session);
                break;
            case "desaprobar":
                desaprobar(obj, session);
                break;
            case "asignarJefe":
                asignarJefe(obj, session);
                break;
            case "asignarJefeMultiple":
                asignarJefeMultiple(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
            case "editarClock":
                editarClock(obj, session);
                break;
            case "invitar":
                invitar(obj, session);
                break;
            case "invitarGrupo":
                invitarGrupo(obj, session);
                break;
            case "desinvitarGrupo":
                desinvitarGrupo(obj, session);
                break;
            case "getInvitacion":
                getInvitacion(obj, session);
                break;
            case "getInvitacionesPendientes":
                getInvitacionesPendientes(obj, session);
                break;
            case "aceptarInvitacion":
                aceptarInvitacion(obj, session);
                break;
            case "getTrabajosProximos":
                getTrabajosProximos(obj, session);
                break;
            case "getTrabajosProximosBoss":
                getTrabajosProximosBoss(obj, session);
                break;
            case "getMisTrabajos":
                getMisTrabajos(obj, session);
                break;
            case "getMisTrabajosEntreFechas":
                getMisTrabajosEntreFechas(obj, session);
                break;
            case "getMisTrabajadores":
                getMisTrabajadores(obj, session);
                break;
            case "getMisTrabajadoresBoss":
                getMisTrabajadoresBoss(obj, session);
                break;
            case "reporteHorasCliente":
                reporteHorasCliente(obj, session);
                break;
            case "isJefe":
                isJefe(obj, session);
                break;
            case "getHistorico":
                getHistorico(obj, session);
                break;
            case "cambiarEvento":
                cambiarEvento(obj, session);
                break;
            case "getHistoricoEntreFechas":
                getHistoricoEntreFechas(obj, session);
                break;
            case "getEventoPerfil":
                getEventoPerfil(obj, session);
                break;
        }
    }

    public static void getEventoPerfil(JSONObject obj, SSSessionAbstract session) {
        try {

            String consulta = "select staff_usuario_get_evento_perfil('" + obj.getString("key") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void isJefe(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select is_jefe('" + obj.getString("key_evento") + "', '" + obj.getString("key_usuario")
                    + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", true);
            if (data == null || data.isEmpty()) {
                obj.put("data", false);
            }

            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all('" + COMPONENT + "') as json";
            if (obj.has("key_staff")) {
                consulta = "select get_all('" + COMPONENT + "', 'key_staff', '" + obj.getString("key_staff")
                        + "') as json";
            }
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_by_key('" + COMPONENT + "', '" + obj.getString("key") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getInvitacion(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = """
                        select to_json(sq1.*) as json
                        FROM (
                            SELECT 
                                staff_usuario.*, 
                                to_json(staff.*) as staff,
                                to_json(evento.*) as evento
                            FROM staff_usuario
                            JOIN staff ON  staff_usuario.key_staff = staff.key
                            JOIN evento ON staff.key_evento = evento.key
                            WHERE staff_usuario.key = '%s'
                        ) sq1

                    """.formatted(obj.getString("key"));

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static JSONObject getByKey(String key) {
        try {
            String consulta = "select get_by_key('" + COMPONENT + "', '" + key + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getByKeyEvento(String key_evento) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_evento', '" + key_evento + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void aprobar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = new JSONObject();
            data.put("key", obj.getString("key_staff_usuario"));
            data.put("fecha_aprobacion", SUtil.now());
            data.put("key_usuario_aprueba", obj.getString("key_usuario"));

            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void asignarJefe(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = new JSONObject();
            data.put("key", obj.getString("key_staff_usuario"));
            data.put("key_usuario_atiende", obj.getString("key_usuario_atiende"));
            data.put("key_usuario_asigna_atiende", obj.getString("key_usuario"));
            data.put("fecha_atiende", SUtil.now());

            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void asignarJefeMultiple(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = new JSONObject();
            // data.put("key", obj.getString("key_staff_usuario"));
            data.put("key_usuario_atiende", obj.getString("key_usuario_atiende"));
            data.put("key_usuario_asigna_atiende", obj.getString("key_usuario"));
            data.put("fecha_atiende", SUtil.now());

            JSONArray staffs = obj.getJSONArray("key_staff_usuario");
            for (int i = 0; i < staffs.length(); i++) {
                data.put("key", staffs.getString(i));
                SPGConect.editObject(COMPONENT, data);
            }
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void desaprobar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = new JSONObject();
            data.put("key", obj.getString("key_staff_usuario"));
            data.put("fecha_aprobacion", SUtil.now());
            data.put("key_usuario_aprueba", "");

            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject staff = Staff.getByKey(obj.getString("key_staff"));
            String consulta = "SELECT\n" +
                    "  to_json(sq.*)::json as json\n" +
                    "FROM (\n" +
                    "  SELECT staff_usuario.*\n" +
                    "  FROM staff_usuario\n" +
                    "    INNER JOIN staff ON staff.key = staff_usuario.key_staff\n" +
                    "  WHERE staff_usuario.estado > 0\n" +
                    "    AND staff.estado > 0\n" +
                    "    AND staff.key = '" + obj.getString("key_staff") + "'\n" +
                    "    AND staff_usuario.key_usuario  = '" + obj.getString("key_usuario") + "'\n" +
                    ") sq";
            JSONObject staffUsuario = SPGConect.ejecutarConsultaObject(consulta);
            if (staffUsuario != null && staffUsuario.has("key")) {
                obj.put("error", "You have already applied for this job. You cannot apply twice.");
                obj.put("estado", "error");
                return;
            }

            // JSONObject usuariosEvento = SPGConect.ejecutarConsultaObject("select
            // get_staff_evento('"+staff.getString("key_evento")+"',
            // '"+obj.getString("key_usuario")+"') as json");
            // if(usuariosEvento!=null && !usuariosEvento.isEmpty()){
            // obj.put("data", usuariosEvento);
            // obj.put("error", "You have already applied for this job. You cannot apply
            // twice.");
            // obj.put("estado", "error");
            // return;
            // }
            JSONObject data = new JSONObject();
            data.put("key", SUtil.uuid());
            data.put("estado", 1);
            data.put("fecha_on", SUtil.now());
            data.put("key_usuario", obj.getString("key_usuario"));
            data.put("key_staff", obj.getString("key_staff"));
            SPGConect.insertArray(COMPONENT, new JSONArray().put(data));

            if (data.has("Telefono")) {
                final String telefono_ = data.getString("Telefono");
                Wtspp.sendInvitacionEvento(telefono_, data);
            }

            if (data.has("Correo")) {

                new Email(
                        new JSONArray().put(
                                data.getString("Correo")),
                        new JSONObject()
                                .put("subject", "New event")
                                .put("path", "mail/registro_exitoso.html"),
                        new JSONObject()
                                .put("key", data.optString("key", "")));
            }

            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void editarClock(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            if (!data.has("fecha_ingreso") || data.isNull("fecha_ingreso")
                    || data.optString("fecha_ingreso").length() == 0) {
                SPGConect.ejecutar("update " + COMPONENT + " set fecha_ingreso = null   where key = '"
                        + data.getString("key") + "'");
                data.remove("fecha_ingreso");
            }

            if (!data.has("fecha_salida") || data.isNull("fecha_salida")
                    || data.optString("fecha_salida").length() == 0) {
                SPGConect.ejecutar("update " + COMPONENT + " set fecha_salida = null   where key = '"
                        + data.getString("key") + "'");
                data.remove("fecha_salida");
            }

            SPGConect.editObject(COMPONENT, data);

            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void invitar(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_usuario_invitado = obj.getString("key_usuario_invitado");
            String key_staff = obj.getString("key_staff");

            // JSONObject staff = Staff.getByKey(key_staff);
            String consulta = "SELECT\n" +
                    "  to_json(sq.*)::json as json\n" +
                    "FROM (\n" +
                    "  SELECT staff_usuario.*\n" +
                    "  FROM staff_usuario\n" +
                    "    INNER JOIN staff ON staff.key = staff_usuario.key_staff\n" +
                    "  WHERE staff_usuario.estado > 0\n" +
                    "    AND staff.estado > 0\n" +
                    "    AND staff.key = '" + key_staff + "'\n" +
                    "    AND staff_usuario.key_usuario  = '" + key_usuario_invitado + "'\n" +
                    ") sq";
            JSONObject staffUsuario = SPGConect.ejecutarConsultaObject(consulta);
            if (staffUsuario != null && staffUsuario.has("key")) {
                obj.put("error", "The user has already applied for this job.");
                obj.put("estado", "error");
                return;
            }

            JSONObject data = new JSONObject();
            data.put("key", SUtil.uuid());
            data.put("estado", 2);
            data.put("fecha_on", SUtil.now());
            data.put("key_usuario", key_usuario_invitado);
            data.put("key_staff", key_staff);
            data.put("fecha_aprobacion", SUtil.now());
            data.put("key_usuario_aprueba", obj.getString("key_usuario"));
            SPGConect.insertObject(COMPONENT, data);

            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void desinvitarGrupo(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONArray usuariosDesinvitadosJson = obj.getJSONArray("key_usuarios_desinvitados");
            String key_staff = obj.getString("key_staff");

            // Validación básica para evitar consultas vacías
            if (usuariosDesinvitadosJson.length() == 0 || key_staff == null || key_staff.isEmpty()) {
                throw new IllegalArgumentException("Los datos proporcionados son inválidos");
            }

            JSONObject send = new JSONObject();
            send.put("component", "staff_usuario");
            send.put("type", "invitarGrupoNotify");
            send.put("estado", "exito");

            // Construir los placeholders para la cláusula IN (tantos '?' como usuarios
            // desinvitados)
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < usuariosDesinvitadosJson.length(); i++) {
                placeholders.append("'" + usuariosDesinvitadosJson.getString(i) + "'");
                if (i < usuariosDesinvitadosJson.length() - 1) {
                    placeholders.append(", ");
                }
                SSServerAbstract.sendUser(send, usuariosDesinvitadosJson.getString(i));
            }

            // Consulta SQL segura
            String consulta = "UPDATE " + COMPONENT + " SET estado = 0 WHERE key_staff = '" + key_staff
                    + "' AND key_usuario IN (" + placeholders.toString() + ")";

            SPGConect.ejecutarUpdate(consulta);

            obj.put("data", usuariosDesinvitadosJson);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void invitarGrupo(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONArray usuarios_invitados = obj.getJSONArray("usuarios_invitados");
            String key_staff = obj.getString("key_staff");

            // JSONObject staff = Staff.getByKey(key_staff);
            String consulta = "SELECT\n" +
                    " jsonb_object_agg(sq.key_usuario, to_json(sq.*))::json as json \n" +
                    "FROM (\n" +
                    "  SELECT staff_usuario.*\n" +
                    "  FROM staff_usuario\n" +
                    "    INNER JOIN staff ON staff.key = staff_usuario.key_staff\n" +
                    "  WHERE staff_usuario.estado > 0\n" +
                    "    AND staff.estado > 0\n" +
                    "    AND staff.key = '" + key_staff + "'\n" +
                    ") sq";
            JSONObject staffUsuario = SPGConect.ejecutarConsultaObject(consulta);
            if (staffUsuario != null && staffUsuario.has("key")) {
                obj.put("error", "The user has already applied for this job.");
                obj.put("estado", "error");
                return;
            }

            JSONObject send = new JSONObject();
            send.put("component", "staff_usuario");
            send.put("type", "invitarGrupoNotify");
            send.put("estado", "exito");

            JSONObject usuario_invitado;
            JSONArray invitados = new JSONArray();
            for (int i = 0; i < usuarios_invitados.length(); i++) {

                usuario_invitado = usuarios_invitados.optJSONObject(i);

                if (usuario_invitado == null) {
                    continue;
                }

                // no existe el usuario
                if (!staffUsuario.has(usuario_invitado.getString("key"))) {

                    JSONObject data = new JSONObject();
                    data.put("key", SUtil.uuid());
                    data.put("estado", 2);
                    data.put("fecha_on", SUtil.now());
                    data.put("key_usuario", usuario_invitado.getString("key"));
                    data.put("key_staff", key_staff);
                    data.put("fecha_aprobacion", SUtil.now());
                    data.put("key_usuario_aprueba", obj.getString("key_usuario"));

                    invitados.put(data);

                    if (usuario_invitado.has("Telefono")) {
                        final String telefono_ = usuario_invitado.getString("Telefono");
                        Wtspp.sendInvitacionEvento(telefono_, data);
                    }

                    if (usuario_invitado.has("Correo")) {
                        new Email(
                                new JSONArray().put(
                                        usuario_invitado.getString("Correo")),
                                new JSONObject()
                                        .put("subject", "New event ")
                                        .put("path", "mail/registro_exitoso.html"),
                                new JSONObject()
                                        .put("key", data.optString("key", "")));

                    }

                    // send.put("data", data);

                    SSServerAbstract.sendUser(send, usuario_invitado.getString("key"));

                }
            }

            SPGConect.insertArray(COMPONENT, invitados);
            obj.put("data", invitados);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getInvitacionesPendientes(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_usuario = obj.getString("key_usuario");

            String consulta = "SELECT get_invitaciones_pendientes('" + key_usuario + "') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void aceptarInvitacion(JSONObject obj, SSSessionAbstract session) {
        try {

            JSONObject staffUSuario = StaffUsuario.getByKey(obj.getString("key_staff_usuario"));

            JSONObject acefalos = SPGConect.ejecutarConsultaObject(
                    "select get_staff_acefalos('" + staffUSuario.getString("key_staff") + "') as json");

            if (acefalos.getInt("acefalos") == 0) {
                SPGConect.ejecutarUpdate("update " + COMPONENT + " set estado = 0 where key_staff = '"
                        + staffUSuario.getString("key_staff") + "' and fecha_aprobacion_invitacion is null ");
                obj.put("estado", "error");
                obj.put("error", "Ya no existe posicion disponible");
                return;

            }

            if (acefalos.getInt("acefalos") == 1) {
                SPGConect.ejecutarUpdate("update " + COMPONENT + " set estado = 0 where key_staff = '"
                        + staffUSuario.getString("key_staff") + "' and fecha_aprobacion_invitacion is null ");
            }

            // Verificamos el staff para saber las fechas de ingreso y salida
            JSONObject staff = Staff.getByKey(staffUSuario.getString("key_staff"));

            // Verificamos si es que tenemos un trabajo en esas fechas
            String fecha_fin = staff.isNull("fecha_fin") ? "9999-12-31" : staff.getString("fecha_fin");
            JSONObject staffUsuario = SPGConect.ejecutarConsultaObject(
                    "select get_staff_usuario_entre_fechas('" + staffUSuario.getString("key_usuario") + "', '"
                            + staff.getString("fecha_inicio") + "', '" + fecha_fin + "') as json");

            if (!staffUsuario.isEmpty()) {
                obj.put("estado", "error");
                obj.put("error", "Ya tienes una posicion en ese horario");
                obj.put("key_staff_usuario", JSONObject.getNames(staffUSuario)[0]);
                return;
            }

            JSONObject usuarioCompany = UsuarioCompany.getByKeyStaff(staff.getString("key"),
                    staffUSuario.getString("key_usuario"));

            JSONObject usuarioStaffFavorito = SPGConect
                    .ejecutarConsultaObject("select to_json(staff_tipo_favorito.*) as json\n" + //
                            "from staff JOIN staff_tipo_favorito\n" + //
                            "on staff.key_staff_tipo = staff_tipo_favorito.key_staff_tipo \n" + //
                            "AND staff_tipo_favorito.key_usuario = '" + usuarioCompany.getString("key_usuario") + "'\n"
                            + //
                            "where staff.key = '" + staffUSuario.getString("key_staff") + "'\n" + //
                            "AND staff_tipo_favorito.key_company = '" + usuarioCompany.getString("key_company") + "'");
            // !! Aqui debo buscar el salario de donde corresponde

            JSONObject data = new JSONObject();
            data.put("key", obj.getString("key_staff_usuario"));
            data.put("fecha_aprobacion_invitacion", SUtil.now());
            data.put("estado", 1);

            if (!usuarioCompany.isNull("salario_hora")) {
                data.put("salario_hora", usuarioCompany.optDouble("salario_hora"));
            }
            if (!usuarioStaffFavorito.isNull("salario")) {
                if (usuarioStaffFavorito.optDouble("salario") > 0) {
                    data.put("salario_hora", usuarioStaffFavorito.optDouble("salario"));
                }
            }

            SPGConect.editObject(COMPONENT, data);

            obj.put("data", data);
            obj.put("estado", "exito");

            JSONObject send = new JSONObject();
            send.put("component", "staff_usuario");
            send.put("type", "invitarGrupoNotify");
            send.put("estado", "exito");
            // send.put("data", data);

            SSServerAbstract.sendAllServer(send.toString());

        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getTrabajosProximos(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_usuario = obj.getString("key_usuario");
            String consulta = "select get_tabajos_proximos('" + key_usuario + "') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getTrabajosProximosBoss(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_usuario = obj.getString("key_usuario");
            String consulta = "select get_tabajos_proximos_boss('" + key_usuario + "') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getMisTrabajos(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mis_trabajos('" + obj.getString("key_usuario") + "') as json";
            if (obj.has("key_evento")) {
                consulta = "select get_mis_trabajos('" + obj.getString("key_evento") + "','"
                        + obj.getString("key_usuario") + "') as json";
            }

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getMisTrabajosEntreFechas(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mis_trabajos('" + obj.getString("key_usuario") + "', '"
                    + obj.getString("fecha_inicio") + "', '" + obj.getString("fecha_fin") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getMisTrabajadores(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mis_trabajadores('" + obj.getString("key_usuario") + "') as json";
            if (obj.has("fecha")) {
                consulta = "select get_mis_trabajadores('" + obj.getString("key_usuario") + "', '"
                        + obj.getString("fecha") + "') as json";
            }

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getMisTrabajadoresBoss(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mis_trabajadores_boss('" + obj.getString("key_usuario") + "', '"
                    + obj.getString("key_staff") + "') as json";

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void reporteHorasCliente(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_trabajadores_cliente('" + obj.getString("key_cliente") + "', '"
                    + obj.getString("fecha_inicio") + "', '" + obj.getString("fecha_fin") + "') as json";

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void cambiarEvento(JSONObject obj, SSSessionAbstract session) {
        try {

            JSONArray data = obj.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject staffUsuario = new JSONObject();
                staffUsuario.put("key", data.getString(i));
                staffUsuario.put("key_staff", obj.getString("key_staff"));
                SPGConect.editObject(COMPONENT, staffUsuario);
            }

            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getHistorico(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_historico('" + obj.getString("key_usuario") + "') as json";

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getHistoricoEntreFechas(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_historico('" + obj.getString("key_usuario") + "', '"
                    + obj.getString("fecha_inicio") + "', '" + obj.getString("fecha_fin") + "') as json";

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}
