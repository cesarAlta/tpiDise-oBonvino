package com.bonvino.bonvino.services;

import com.bonvino.bonvino.Models.Bodega;
import com.bonvino.bonvino.Models.Usuario;
import com.bonvino.bonvino.Models.Vino;
import javafx.application.Platform;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;
import java.util.List;

public class InterfazNotificacionPush {



    public static void NotificarNovedadesAEnofiloSuscriptosBodega(List<Vino> actualizados, int size, List<Vino> vinosCreados, int size1, String nombre, List<String> usuariosANotificar) {
        String nota = "Hay "+size+" vinos actualizados y "+size1+" vinos nuevos para la bodega "+ nombre;

        for (String us: usuariosANotificar) {
            System.out.println("Notificacion para "+ us + ": "+ nota);
        }
    }


}
