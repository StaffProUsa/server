package Component;

import org.json.JSONArray;
import org.json.JSONObject;

import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
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
            case "editar":
                editar(obj, session);
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
            case "getInvitacionesPendientes":
                getInvitacionesPendientes(obj, session);
                break;
            case "aceptarInvitacion":
                aceptarInvitacion(obj, session);
                break;
            case "getTrabajosProximos":
                getTrabajosProximos(obj, session);
                break;
            case "getMisTrabajos":
                getMisTrabajos(obj, session);
                break;
            case "getHistorico":
                getHistorico(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all('" + COMPONENT + "') as json";
            if(obj.has("key_staff")){
                consulta = "select get_all('" + COMPONENT + "', 'key_staff', '"+obj.getString("key_staff")+"') as json";
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
            String consulta = "select get_by_key('" + COMPONENT + "', '"+obj.getString("key")+"') as json";
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
            String consulta = "select get_by_key('" + COMPONENT + "', '"+key+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getByKeyEvento(String key_evento) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_evento', '"+key_evento+"') as json";
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
            if(staffUsuario != null && staffUsuario.has("key")) {
                obj.put("error", "You have already applied for this job. You cannot apply twice.");
                obj.put("estado", "error");
                return;
            }

            // JSONObject usuariosEvento = SPGConect.ejecutarConsultaObject("select get_staff_evento('"+staff.getString("key_evento")+"', '"+obj.getString("key_usuario")+"') as json");
            // if(usuariosEvento!=null && !usuariosEvento.isEmpty()){
            //     obj.put("data", usuariosEvento);
            //     obj.put("error", "You have already applied for this job. You cannot apply twice.");
            //     obj.put("estado", "error");
            //     return;
            // }
            JSONObject data = new JSONObject();
            data.put("key", SUtil.uuid());
            data.put("estado", 1);
            data.put("fecha_on", SUtil.now());
            data.put("key_usuario", obj.getString("key_usuario"));
            data.put("key_staff", obj.getString("key_staff"));
            SPGConect.insertArray(COMPONENT, new JSONArray().put(data));
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
            if(staffUsuario != null && staffUsuario.has("key")) {
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
            SPGConect.insertArray(COMPONENT, new JSONArray().put(data));
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
            

            // Construir los placeholders para la cláusula IN (tantos '?' como usuarios desinvitados)
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < usuariosDesinvitadosJson.length(); i++) {
                placeholders.append("'"+usuariosDesinvitadosJson.getString(i)+"'");
                if (i < usuariosDesinvitadosJson.length() - 1) {
                    placeholders.append(", ");
                }
                SSServerAbstract.sendUser(send, usuariosDesinvitadosJson.getString(i));
            }

            // Consulta SQL segura
            String consulta = "UPDATE " + COMPONENT + " SET estado = 0 WHERE key_staff = '"+key_staff+"' AND key_usuario IN (" + placeholders.toString() + ")";

            
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
            JSONArray key_usuarios_invitados = obj.getJSONArray("key_usuarios_invitados");
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
            if(staffUsuario != null && staffUsuario.has("key")) {
                obj.put("error", "The user has already applied for this job.");
                obj.put("estado", "error");
                return;
            }

            JSONObject send = new JSONObject();
            send.put("component", "staff_usuario");
            send.put("type", "invitarGrupoNotify");
            send.put("estado", "exito");
            

            String key_usuario_invitado;
            JSONArray invitados = new JSONArray();
            for (int i = 0; i < key_usuarios_invitados.length(); i++) {
                key_usuario_invitado = key_usuarios_invitados.getString(i);

                // no existe el usuario
                if(!staffUsuario.has(key_usuario_invitado)){

                    JSONObject data = new JSONObject();
                    data.put("key", SUtil.uuid());
                    data.put("estado", 2);
                    data.put("fecha_on", SUtil.now());
                    data.put("key_usuario", key_usuario_invitado);
                    data.put("key_staff", key_staff);
                    data.put("fecha_aprobacion", SUtil.now());
                    data.put("key_usuario_aprueba", obj.getString("key_usuario"));
                    invitados.put(data);

                    //send.put("data", data);

                    SSServerAbstract.sendUser(send, key_usuario_invitado);

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

            String consulta = "SELECT\n" +
                            "  array_to_json(array_agg(sq.*))::json as json \n" +
                            "FROM (\n" +
                            "   SELECT to_json(staff_usuario.*)::json as staff_usuario,\n" +
                            "      to_json(staff.*)::json as staff,\n" + 
                            "      to_json(evento.*)::json as evento,\n" + 
                            "      to_json(staff_tipo.*)::json as staff_tipo,\n" + 
                            "      to_json(cliente.*)::json as cliente,\n" + 
                            "      to_json(company.*)::json as company\n" +
                            "   FROM staff_usuario\n" +
                            "      INNER JOIN staff ON staff.key = staff_usuario.key_staff \n" + 
                            "      INNER JOIN evento ON staff.key_evento = evento.key\n" + 
                            "      left JOIN cliente ON evento.key_cliente = cliente.key\n" + 
                            "      INNER JOIN staff_tipo ON staff.key_staff_tipo = staff_tipo.key\n" +
                            "      INNER JOIN company ON company.key = evento.key_company\n" +
                            "   WHERE staff_usuario.estado  = 2\n" + 
                            "     AND staff.estado > 0\n" +
                            "     AND evento.estado > 0\n" +
                            "     AND evento.fecha::date >= now()::date\n" +
                            "     AND cliente.estado > 0\n" +
                            "     AND staff_tipo.estado > 0\n" +
                            "     AND staff_usuario.key_usuario  = '" +  key_usuario+ "'\n" +
                            "     AND company.estado  > 0\n" +
                            ") sq";
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


            JSONObject acefalos = SPGConect.ejecutarConsultaObject("select get_staff_acefalos('"+staffUSuario.getString("key_staff")+"') as json");

            if(acefalos.getInt("acefalos")==0){
                SPGConect.ejecutarUpdate("update "+COMPONENT+" set estado = 0 where key_staff = '"+staffUSuario.getString("key_staff")+"' and fecha_aprobacion_invitacion is null ");
                obj.put("estado", "error");
                obj.put("error", "Ya no existe posicion disponible");
                return;
            }
            
            if(acefalos.getInt("acefalos")==1){
                SPGConect.ejecutarUpdate("update "+COMPONENT+" set estado = 0 where key_staff = '"+staffUSuario.getString("key_staff")+"' and fecha_aprobacion_invitacion is null ");
            }

            JSONObject data = new JSONObject();
            data.put("key", obj.getString("key_staff_usuario"));
            data.put("fecha_aprobacion_invitacion", SUtil.now());
            data.put("estado", 1);
            
            SPGConect.editObject(COMPONENT, data);
           
            obj.put("data", data);
            obj.put("estado", "exito");

            JSONObject send = new JSONObject();
            send.put("component", "staff_usuario");
            send.put("type", "invitarGrupoNotify");
            send.put("estado", "exito");
            

            //send.put("data", data);

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

            String consulta = "select get_tabajos_proximos('"+key_usuario+"') as json";
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
            String consulta = "select get_mis_trabajos('"+obj.getString("key_usuario")+"') as json";
            if(obj.has("key_evento")){
                consulta = "select get_mis_trabajos('"+obj.getString("key_evento")+"','"+obj.getString("key_usuario")+"') as json";
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

    public static void getHistorico(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_historico('"+obj.getString("key_usuario")+"') as json";
            
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
