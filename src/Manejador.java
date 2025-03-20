import Component.*;
import Servisofts.SConsole;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;

public class Manejador {
    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        if (session != null) {
            SConsole.log(session.getIdSession(), "\t|\t", obj.getString("component"), obj.getString("type"));
        } else {
            SConsole.log("NoSocketSession", "\t|\t", obj.getString("component"), obj.getString("type"));
        }
        if (obj.isNull("component")) {
            return;
        }
        switch (obj.getString("component")) {
            case usuario.COMPONENT: usuario.onMessage(obj, session); break;
            case Evento.COMPONENT: Evento.onMessage(obj, session); break;
            case Actividad.COMPONENT: Actividad.onMessage(obj, session); break;
            case TipoEntrada.COMPONENT: TipoEntrada.onMessage(obj, session); break;
            case Entrada.COMPONENT: Entrada.onMessage(obj, session); break;
            case Mesa.COMPONENT: Mesa.onMessage(obj, session); break;
            case Sector.COMPONENT: Sector.onMessage(obj, session); break;
            case Banner.COMPONENT: Banner.onMessage(obj, session); break;
            case Venta.COMPONENT: Venta.onMessage(obj, session); break;
            case orden_pago.COMPONENT: orden_pago.onMessage(obj, session); break;
            case tarjeta.COMPONENT: tarjeta.onMessage(obj, session); break;
            case Parametro.COMPONENT: Parametro.onMessage(obj, session); break;
            case Pasarela.COMPONENT: Pasarela.onMessage(obj, session); break;
            case SolicitudQr.COMPONENT: SolicitudQr.onMessage(obj, session); break;
            case Reportes.COMPONENT: Reportes.onMessage(obj, session); break;
            case Enviroment.COMPONENT: new Enviroment(obj, session); break;
            case MesaCompra.COMPONENT: MesaCompra.onMessage(obj, session); break;
            case StaffTipo.COMPONENT: StaffTipo.onMessage(obj, session); break;
            case Staff.COMPONENT: Staff.onMessage(obj, session); break;
            case StaffUsuario.COMPONENT: StaffUsuario.onMessage(obj, session); break;
            case Company.COMPONENT: Company.onMessage(obj, session); break;
            case Cliente.COMPONENT: Cliente.onMessage(obj, session); break;
            case StaffSector.COMPONENT: StaffSector.onMessage(obj, session); break;
            case UsuarioCompany.COMPONENT: UsuarioCompany.onMessage(obj, session); break;
            case StaffTipoFavorito.COMPONENT: StaffTipoFavorito.onMessage(obj, session); break;
            case Asistencia.COMPONENT: Asistencia.onMessage(obj, session); break;
            case Invitacion.COMPONENT: Invitacion.onMessage(obj, session); break;
            case StaffTipoCompany.COMPONENT: StaffTipoCompany.onMessage(obj, session); break;
            case Board.COMPONENT: Board.onMessage(obj, session); break;
            /*case General.COMPONENT:
                General.onMessage(obj, session);
                break;*/
        }
    }
}
