package com.bonvino.bonvino.Controllers;

import com.bonvino.bonvino.DTOs.MaridajeDTO;
import com.bonvino.bonvino.DTOs.VarietalDTO;
import com.bonvino.bonvino.DTOs.VinoDataDTO;
import com.bonvino.bonvino.DTOs.VinoInfoDTO;
import com.bonvino.bonvino.Models.*;
import com.bonvino.bonvino.Repositories.*;
import com.bonvino.bonvino.Views.PantallaActualizacionVino;
import com.bonvino.bonvino.services.ApiBodega;
import com.bonvino.bonvino.services.InterfazNotificacionPush;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class GestorActualizacionVIno {
    @Autowired
    IBodegaRepository iBodegaRepository;
    @Autowired
    IVinoRepository iVinoRepository;
    @Autowired
    ITipoUvaRepository iTipoUvaRepository;
    @Autowired
    IMaridajeRepository iMaridajeRepository;
    @Autowired
    IEnofiloRepository iEnofiloRepository;
    @Autowired
    ApiBodega apiBodega;
    @Autowired
    EntityManager entityManager;
    private PantallaActualizacionVino pantallaActualizacionVino;
    private LocalDateTime fechaActual;
    private Map<String, Bodega> bodegaNombreBodegaMap;
    private List<VinoDataDTO> vinosRecibidos;
    private List<Vino> vinosBodegaSeleccionada;
    private List<Vino> vinoActualizableList;
    private Bodega bodegaSeleccionada;


    /**
     * Inicia el proceso de actualizacion de vinos.
     * <p>
     * Establece la dependencia con la pantalla de ACTUALIZACION VINO, e
     * inicia el proceso de importacion para la actualizacion de vinos.
     *
     * @param pantallaActualizacionVino pantalla para bindear con el gestor.
     */
    public void opcionImportarActualizacionVinos(PantallaActualizacionVino pantallaActualizacionVino) {
        this.pantallaActualizacionVino = pantallaActualizacionVino;
        buscarBodegasPtesActualizar();
    }

    /**
     * Busca las bodegas con actualizacion disponible.
     * Recorre todas las bodegas validadno la periodicidad. Si esta en periodo de actualizacion
     * se genera un map key:nombre, value: bodega, y lo envia a la pantalla.
     */

    private void buscarBodegasPtesActualizar() {
        bodegaNombreBodegaMap = new HashMap<>();
        getFechaActual();

        for (Bodega bodega : iBodegaRepository.findAll()) {
            if (bodega.validarPeriodicidad(fechaActual.toLocalDate())) {
                bodegaNombreBodegaMap.put(bodega.getNombre(), bodega);
            }
        }


        // Alternaivo A1
//      Set<String> sinBodega = new HashSet<>();
//      pantallaActualizacionVino.mostrarBodegasActualizables(sinBodega);

        pantallaActualizacionVino.mostrarBodegasActualizables(bodegaNombreBodegaMap.keySet());

    }

    /**
     * Obtiene la fecha y hora actual, y la setea al atributo 'fechaActual'
     */
    private void getFechaActual() {
        fechaActual = LocalDateTime.now();
    }

    /**
     * Recibe la bodega seleccionada e inicia el proceso de actualizacion - creacion de vinos.
     *
     * @param nombreBodegaSel type string
     */
    @Transactional
    public void tomarSeleccionBodegaPteActualizar(String nombreBodegaSel) {
        this.bodegaSeleccionada = this.bodegaNombreBodegaMap.get(nombreBodegaSel);
        this.bodegaSeleccionada = entityManager.merge(bodegaSeleccionada);
        obtenerActualizacionesBodegaSeleccionada();
    }

    private void obtenerActualizacionesBodegaSeleccionada() {
        Map<String, Object> respApiVinos = apiBodega.solicitarActualizacionVinos(bodegaSeleccionada);
        // Alternativa CU5 A3.
        if (respApiVinos.containsKey("error")) {
            pantallaActualizacionVino.SinRespuestaApi(respApiVinos.get("error").toString());
        } else {
            vinosRecibidos = (List<VinoDataDTO>) respApiVinos.get("vinos");
            actualizarNovedadesVinos();
        }

    }

    /**
     * A partir de la lista de vinos de la bodega seleccionada brindada por el repositorio de JPA
     * obtengo una lista de vinos actualizables de la bodega.
     */
    private void actualizarNovedadesVinos() {
        vinosBodegaSeleccionada = iVinoRepository.findByBodega(bodegaSeleccionada);
        vinoActualizableList = new ArrayList<>();

        for (VinoDataDTO vinoData : vinosRecibidos) {
            bodegaSeleccionada.esTuVino(vinoData.getNombre(), vinoData.getAñada(), vinosBodegaSeleccionada)
                    .ifPresent(v -> {
                        vinoActualizableList.add(v);
                        vinoData.setActualizable(true);
                    });

        }
        actualizarOCrearVinoBodega();
    }

    /**
     * Agregando el @Transactional de JPA nos permite hacer un rollback en caso de que falle la escritura en la BD.
     * Metodo que procesa la actualizacion o creacion de vinos.
     */
    @Transactional
    private void actualizarOCrearVinoBodega() {
        List<Vino> creados = new ArrayList<>();

        for (VinoDataDTO vinoRecibido : vinosRecibidos) {
            if (vinoRecibido.isActualizable()) {
                actualizarVinoExistente(vinoRecibido, vinoActualizableList);
            } else {
                Vino v = crearVino(vinoRecibido);
                vinosBodegaSeleccionada.add(v);
                creados.add(v);
            }
        }
        bodegaSeleccionada.setFechaUltimaActualizacion(fechaActual.toLocalDate());
        iVinoRepository.saveAll(vinosBodegaSeleccionada);

        ordenarInformacionAMostrar();

    }

    /**
     * Actualiza los atributos actualizables de un vino; toma el vino recibido de la API
     * y los vinos de la bodega seleccionada y se los pasa por parametro a la bodega seleccionada
     * para que se actualicen los datos actualizables del vino.
     * SI BIEN EL GESTOR ES EL QUE TIENE LOS DATOS PARA ACTUALIZAR EL VINO EN CUESTION DELEGA ESTA
     * ACCION A LA BODEGA, ASI REDUCE ACOPLAMIENTO. PATRON EXPERTO EN INFORMACION.
     *
     * @param vinoRecibido
     * @param vinoBodegaSeleccionadaList
     */
    private void actualizarVinoExistente(VinoDataDTO vinoRecibido, List<Vino> vinoBodegaSeleccionadaList) {

        Map<String, Object> vinoDataMap =
                Map.of(
                        "nombre", vinoRecibido.getNombre(),
                        "añada", vinoRecibido.getAñada(),
                        "imagenEtiqueta", vinoRecibido.getImagenEtiqueta(),
                        "notaDeCataBodega", vinoRecibido.getNotaDeCataBodega(),
                        "precioARS", vinoRecibido.getPrecioARS()
                );

        bodegaSeleccionada.actualizarDatosVino(vinoDataMap, vinoBodegaSeleccionadaList, fechaActual);
    }


    private void ordenarInformacionAMostrar() {
        List<VinoInfoDTO> vinoInfoDTOS = new ArrayList<>();

        int vinosActualizados = 0;
        int vinosNuevos = 0;

        for (Vino v : vinosBodegaSeleccionada) {
            if (v.getFechaActualizacion() != null) {
                vinosActualizados++;
            } else {
                vinosNuevos++;
            }

            vinoInfoDTOS.add(
                    VinoInfoDTO.builder()
                            .nombreYañada(v.getNombre() + "-" + v.getAñada())
                            .imagenEtiqueta(v.getImagenEtiqueta())
                            .notaCataDeBodega(v.getNotaDeCataBodega())
                            .varietalPrincipal(v.getVarietales()
                                    .stream()
                                    .max(Comparator.comparingDouble(Varietal::getPorcentajeComposicion))
                                    .map(vari -> vari.getTipoUva().getNombre())
                                    .orElse("No se pudo cargar el varietal principal"))
                            .varietales(
                                    v.getVarietales().stream().map(
                                            vari -> vari.getTipoUva().getNombre() + " " + vari.getPorcentajeComposicion() + " %"
                                    ).toList())
                            .maridajes(
                                    v.getMaridajes().stream().map(
                                            m -> m.getNombre() + ": " + m.getDescripcion()
                                    ).toList()
                            )
                            .fechaActualizacion(v.getFechaActualizacion() != null ? v.getFechaActualizacion() : null)
                            .nombreBodeda(v.getBodega().getNombre())
                            .precio(v.getPrecioARS().toString())
                            .build()
            );

        }

        pantallaActualizacionVino.mostrarResumenVinosimportados(vinoInfoDTOS, bodegaSeleccionada.getNombre());

        obtenerSeguidoresBodega(vinosActualizados, vinosNuevos, bodegaSeleccionada.getNombre());
    }


    /**
     * inica el proceso de notificacion a los enofilos suscriptos a la bodega que se realizo la actualizacion.
     *
     * @param vinosActualizados
     * @param vinoNuevos
     * @param nombreBodega
     */
    private void obtenerSeguidoresBodega(int vinosActualizados, int vinoNuevos, String nombreBodega) {
        List<String> usuariosANotificar = iEnofiloRepository.findAll()
                .stream()
                .filter(e -> e.seguisABodega(bodegaSeleccionada))
                .map(Enofilo::getNombreUsuario)
                .toList();
        InterfazNotificacionPush.NotificarNovedadesAEnofiloSuscriptosBodega(vinosActualizados, vinoNuevos, bodegaSeleccionada.getNombre(), usuariosANotificar);

        finCU();

    }

    /**
     * Crea y retorna un Objeto Vino.
     * En este caso el gestor tiene todos los datos para crear el vino.
     * Aunque la creacion de los varietales se lo delega al vino; el gestor busca la informacion necesaria para crear los
     * varietales y se lo envia al vino para que este lo realice.
     * El vino ademas tiene  una relacion de 1..* esto significa que al crear el vino hay que crear y asignarle al menos un varietal.
     * Vemos el oatron controlador con el patron creador.
     *
     * @param vinoRecibido
     * @return
     */

    private Vino crearVino(VinoDataDTO vinoRecibido) {
        Map<TipoUva, VarietalDTO> tipoUvaVarietalDTOMap = buscarTipoUva(vinoRecibido.getVarietales());
        List<Maridaje> maridaje = buscarMaridaje(vinoRecibido.getMaridajes());

        Vino nuevoVino = new Vino(
                vinoRecibido.getAñada(),
                vinoRecibido.getImagenEtiqueta(),
                vinoRecibido.getNombre(),
                vinoRecibido.getNotaDeCataBodega(),
                vinoRecibido.getPrecioARS(),
                bodegaSeleccionada,
                maridaje);

        nuevoVino.crearVarietal(tipoUvaVarietalDTOMap);
        return nuevoVino;
    }

    /**
     * Toma los maridajes  del vino recibido, para obtenerlos y setearlos.
     * Retorna una lista de maridajes.
     * No encuentra el maridaje lanza una excepcion, para el manejo de esta en las proximas versiones.
     *
     * @param maridajeVinoRecibido
     * @return
     */

    private List<Maridaje> buscarMaridaje(List<MaridajeDTO> maridajeVinoRecibido) {
        List<Maridaje> maridajes = new ArrayList<>();
        List<Maridaje> maridajesExistentes = iMaridajeRepository.findAll();

        for (MaridajeDTO maridajeRecibido : maridajeVinoRecibido) {
            Maridaje m = maridajesExistentes
                    .stream()
                    .filter(maridaje -> maridaje.sosMaridaje(maridajeRecibido.getNombre()))
                    .findFirst()
                    .orElseThrow();
            maridajes.add(m);
        }
        return maridajes;
    }

    /**
     * Toma los varietales del vino recibido, para obtener el tipo de uva. lo agrega a un
     * Map key:tipoUva, value: Varietal. Para luego retornarlo.
     * No encuentra el tipo uva lanza una excepcion, para el manejo de esta en las proximas versiones.
     *
     * @param varietalRecibidoList
     * @return
     */
    private Map<TipoUva, VarietalDTO> buscarTipoUva(List<VarietalDTO> varietalRecibidoList) {
        Map<TipoUva, VarietalDTO> tipoUvaVarietalDTOMap = new HashMap<>();

        List<TipoUva> tipoUvaList = iTipoUvaRepository.findAll();

        for (VarietalDTO varietalDTO : varietalRecibidoList) {
            TipoUva tipoUva = tipoUvaList
                    .stream()
                    .filter(tu -> tu.sosTipoUva(varietalDTO.getNombreTipoUva()))
                    .findFirst()
                    .orElseThrow();
            tipoUvaVarietalDTOMap.put(tipoUva, varietalDTO);
        }
        return tipoUvaVarietalDTOMap;
    }

    private void finCU() {
        vinoActualizableList = null;
        bodegaSeleccionada = null;
        vinosRecibidos = null;
        vinoActualizableList = null;
        bodegaNombreBodegaMap = null;


    }

}
