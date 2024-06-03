package com.bonvino.bonvino.services;

import com.bonvino.bonvino.Models.Bodega;
import com.bonvino.bonvino.Models.Usuario;
import com.bonvino.bonvino.Models.Vino;
import javafx.application.Platform;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;
import java.util.List;

public class InterfazNotificacionPush {


    public static void NotificarNovedadesAEnofiloSuscriptosBodega(int vinosActualizados, int vinosNuevos, String nombreBodega, List<String> usuariosANotificar) {
        String nota = vinosActualizados > 1
                ? "Hay " + vinosActualizados + " vinos actualizados"
                : "Hay " + vinosActualizados + " vino actualizado";

        nota += vinosNuevos > 0
                ? vinosNuevos > 1 ? " y " + vinosNuevos + " vinos nuevos" : " y 1 vino nuevo"
                : "";
        nota += " para la bodega " + nombreBodega;

        for (String us : usuariosANotificar) {
            System.out.println("Notificacion para " + us + ": " + nota);
        }
    }


}
