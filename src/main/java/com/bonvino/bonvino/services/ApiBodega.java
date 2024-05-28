package com.bonvino.bonvino.services;

import com.bonvino.bonvino.DTOs.VinoDTO;
import com.bonvino.bonvino.Models.Bodega;
import com.bonvino.bonvino.Models.Vino;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiBodega {
    @Value("${spring-external-service.bodega-url}")
    private String api;
    @Autowired
    RestTemplate restTemplate;

    public List<VinoDTO> getApiResponse() {
        VinoDTO[] places = restTemplate.getForObject(api + "/vino", VinoDTO[].class);
        assert places != null;
        return Arrays.asList(places);
    }

    public  Map<String, Object> solicitarActualizacionVinos(Bodega bodegaSeleccionada) {
        return obtenerActualizacionVinos(bodegaSeleccionada.getNombre());
    }

    private  Map<String, Object> obtenerActualizacionVinos(String nombreBodega) {
        Map<String, Object> resp = new HashMap<>();


        // Construye la URL de la API usando el nombre de la bodega
        String url = api + "/" + nombreBodega;
//Socicitud HTTP GET a una URL y obtengo un array de vinos. Capturamos el error si sucede.
        try {
            Vino[] vino = restTemplate.getForObject(url + "/obtenerVinos", Vino[].class);
            if (vino == null) {
                throw new RuntimeException("La respuesta de la API es nula");
            }
            // Convierte el array de objetos Vino en una lista y la retorna
            resp.put("vinos", Arrays.asList(vino));
        } catch (ResourceAccessException e) {
            resp.put("error","Error de conexión al obtener los vinos de la bodega: " + nombreBodega);

            // Maneja los errores de acceso a recursos
//            throw new RuntimeException("Error de conexión al obtener los vinos de la bodega: " + nombreBodega, e);

        }
return resp;

    }
}
