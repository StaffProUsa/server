package Component;

import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.SConsole;
import Servisofts.SPGConect;

// Recordatorios push cada 5 minutos. push_log evita enviar dos veces el mismo aviso.
// Solo trabajadores confirmados: staff_usuario.estado = 1 con fecha_aprobacion_invitacion.
public class PushScheduler extends Thread {
    private static final long INTERVALO = 5 * 60 * 1000;

    private static final String CONFIRMADOS = "  from staff_usuario su\n" +
            "    join staff s on s.key = su.key_staff\n" +
            "    join evento e on e.key = s.key_evento\n" +
            "    left join staff_tipo st on st.key = s.key_staff_tipo\n" +
            "  where su.estado = 1 and su.fecha_aprobacion_invitacion is not null\n" +
            "    and s.estado > 0 and e.estado > 0\n";

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(INTERVALO);
                if (!Push.enabled()) continue;
                recordatorio2h();
                sinClockInTrabajador();
                sinClockInJefe();
            } catch (Exception e) {
                SConsole.error("[push] " + e.getLocalizedMessage());
            }
        }
    }

    // #5 El turno empieza en ~2 h -> trabajador
    private void recordatorio2h() throws Exception {
        JSONArray rows = SPGConect.ejecutarConsultaArray("select coalesce(json_agg(sq.*), '[]') as json from (\n" +
                "  select su.key, su.key_usuario, s.key as key_staff, e.key as key_evento, e.descripcion as evento, st.descripcion as puesto\n" +
                CONFIRMADOS +
                "    and s.fecha_inicio > now() + interval '90 minutes' and s.fecha_inicio <= now() + interval '2 hours'\n" +
                "    and not exists (select 1 from push_log p where p.tipo = 'recordatorio_2h' and p.ref_key = su.key and p.key_usuario = su.key_usuario)\n" +
                ") sq");
        for (int i = 0; i < rows.length(); i++) {
            JSONObject r = rows.getJSONObject(i);
            Push.aUsuario(r.getString("key_usuario"), null,
                    "Your shift starts in 2 hours", "Tu turno empieza en 2 horas",
                    Push.puestoEn(r) + ".",
                    Push.puestoEs(r) + ".",
                    "/evento?key=" + r.optString("key_evento"));
            Push.registrar(r.getString("key_usuario"), "recordatorio_2h", r.getString("key"));
        }
    }

    // #6 15 min después del inicio sin clock in -> trabajador
    private void sinClockInTrabajador() throws Exception {
        JSONArray rows = SPGConect.ejecutarConsultaArray("select coalesce(json_agg(sq.*), '[]') as json from (\n" +
                "  select su.key, su.key_usuario, e.descripcion as evento, st.descripcion as puesto\n" +
                CONFIRMADOS +
                "    and su.fecha_ingreso is null\n" +
                "    and s.fecha_inicio <= now() - interval '15 minutes' and s.fecha_inicio > now() - interval '45 minutes'\n" +
                "    and not exists (select 1 from push_log p where p.tipo = 'sin_clockin' and p.ref_key = su.key and p.key_usuario = su.key_usuario)\n" +
                ") sq");
        for (int i = 0; i < rows.length(); i++) {
            JSONObject r = rows.getJSONObject(i);
            Push.aUsuario(r.getString("key_usuario"), null,
                    "Don't forget to clock in", "No olvides marcar tu entrada",
                    "Your shift for " + Push.puestoEn(r) + " already started.",
                    "Tu turno de " + Push.puestoEs(r) + " ya empezó.",
                    "/event_in_progress");
            Push.registrar(r.getString("key_usuario"), "sin_clockin", r.getString("key"));
        }
    }

    // #6b 30 min después del inicio sin clock in -> admin que invitó y jefe (un aviso por puesto)
    private void sinClockInJefe() throws Exception {
        JSONArray rows = SPGConect.ejecutarConsultaArray("select coalesce(json_agg(sq.*), '[]') as json from (\n" +
                "  select d.destino, d.key_staff, d.key_evento, d.evento, d.puesto, count(*) as cantidad,\n" +
                "         min(d.key_usuario) as key_usuario\n" +
                "  from (\n" +
                "    select su.key_usuario, s.key as key_staff, e.key as key_evento, e.descripcion as evento,\n" +
                "           st.descripcion as puesto, unnest(array[su.key_usuario_aprueba, su.key_usuario_atiende]) as destino\n" +
                CONFIRMADOS +
                "      and su.fecha_ingreso is null\n" +
                "      and s.fecha_inicio <= now() - interval '30 minutes' and s.fecha_inicio > now() - interval '90 minutes'\n" +
                "  ) d\n" +
                "  where d.destino is not null and d.destino <> '' and d.destino <> d.key_usuario\n" +
                "    and not exists (select 1 from push_log p where p.tipo = 'sin_clockin_jefe' and p.ref_key = d.key_staff and p.key_usuario = d.destino)\n" +
                "  group by d.destino, d.key_staff, d.key_evento, d.evento, d.puesto\n" +
                ") sq");
        for (int i = 0; i < rows.length(); i++) {
            JSONObject r = rows.getJSONObject(i);
            int cantidad = r.getInt("cantidad");
            String quienEn = cantidad == 1 ? Push.nombre(r.getString("key_usuario")) + " hasn't" : cantidad + " workers haven't";
            String quienEs = cantidad == 1 ? Push.nombre(r.getString("key_usuario")) + " no ha" : cantidad + " trabajadores no han";
            Push.aUsuario(r.getString("destino"), null,
                    "Missing clock-in", "Falta marcar entrada",
                    quienEn + " clocked in for " + Push.puestoEn(r) + ".",
                    quienEs + " marcado entrada en " + Push.puestoEs(r) + ".",
                    "/company/event?key_evento=" + r.optString("key_evento"));
            Push.registrar(r.getString("destino"), "sin_clockin_jefe", r.getString("key_staff"));
        }
    }
}
