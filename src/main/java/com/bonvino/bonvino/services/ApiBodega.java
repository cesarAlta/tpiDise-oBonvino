package com.bonvino.bonvino.services;

import com.bonvino.bonvino.DTOs.VinoDataDTO;
import com.bonvino.bonvino.Models.Bodega;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * ApiBodega usa RestTemplate es una clase de SPRING que facilita la comunicacion don servicios RESTful.
 * Convierte automaticamente las respuestasd HTTP a objetos java.
 * Utiliza HttpMessageConverers para trnasformar datos JSON.
 * Ademas brinda manejo de erroes.
 */
@Service
public class ApiBodega {

    @Value("${spring-external-service.bodega-url}")
    private String URLApiBodega;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Inicia el proceso de solicitar a la API de bodegas la lista de vinos.
     *
     * @param bodegaSeleccionada
     * @return map a priori, key:"error" o key:"vinos" para capturar el error.
     *
     * Metodo a mejorar con la implementacion de manejo de errores.
     */

    public Map<String, Object> solicitarActualizacionVinos(Bodega bodegaSeleccionada) {
        String nombreBodega = bodegaSeleccionada.getNombre();
        Map<String, Object> resp = new HashMap<>();
        String url = URLApiBodega + "/" + nombreBodega;

        try {
            VinoDataDTO[] vinoData = restTemplate.getForObject(url + "/obtenerVinos", VinoDataDTO[].class);
            if (vinoData == null) {
                throw new RuntimeException("La respuesta de la API es nula");
            }
            resp.put("vinos", Arrays.asList(vinoData));
            // Flujo alternativo A3.
        } catch (ResourceAccessException e) {
            resp.put("error", "Error de conexión al obtener los vinos de la bodega: " + nombreBodega);
        }
        return resp;

    }
}
