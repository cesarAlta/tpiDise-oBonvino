package com.bonvino.bonvino.services;

import com.bonvino.bonvino.DTOs.VinoDTO;
import com.bonvino.bonvino.Models.Bodega;
import com.bonvino.bonvino.Models.Vino;
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
    RestTemplate restTemplate;

//    public List<VinoDTO> getApiResponse() {
//        VinoDTO[] places = restTemplate.getForObject(URLApiBodega + "/vino", VinoDTO[].class);
//        assert places != null;
//        return Arrays.asList(places);
//    }

    /**
     * Inicia el proceso de solicitar a la API de bodegas la lista de vinos.
     *
     * @param bodegaSeleccionada
     * @return map a priori, key:"error" o key:"vinos" para capturar el error.
     * <p>
     * Metodo a mejorar con la implementacion de manejo de errores.
     */

    public Map<String, Object> solicitarActualizacionVinos(Bodega bodegaSeleccionada) {
        String nombreBodega = bodegaSeleccionada.getNombre();
        Map<String, Object> resp = new HashMap<>();

        // Construye la URL de la API usando el nombre de la bodega
        String url = URLApiBodega + "/" + nombreBodega;

        //Socicitud HTTP GET a una URL y obtengo un array de vinos. Capturamos el error si sucede.

        try {
            Vino[] vino = restTemplate.getForObject(url + "/obtenerVinos", Vino[].class);
            if (vino == null) {
                throw new RuntimeException("La respuesta de la API es nula");
            }
            // Convierte el array de objetos Vino en una lista y la retorna
            resp.put("vinos", Arrays.asList(vino));

            // Flujo alternativo A3.
        } catch (ResourceAccessException e) {
            resp.put("error", "Error de conexión al obtener los vinos de la bodega: " + nombreBodega);

            // Maneja los errores de acceso a recursos
//            throw new RuntimeException("Error de conexión al obtener los vinos de la bodega: " + nombreBodega, e);

        }
        return resp;

    }
}
