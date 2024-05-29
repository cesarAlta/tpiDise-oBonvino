package com.bonvino.bonvino.Controllers;

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
//    iny dependencias
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
//    atributos
    private PantallaActualizacionVino pantallaActualizacionVino;
    private  LocalDateTime fechaActual;
    private  Map<String, Bodega> bodegaNombreBodegaMap;
    //    vinos recibidos desde la api
    private  List<Vino> vinosRecibidos;
    //    vinos registrados de la bodega seleccionada.
    private List<Vino> vinosBodegaSeleccionada;
    //    vinos para actualizar.
    private List<Vino> vinoActualizableList;

    private Bodega bodegaSeleccionada;


    /**
     * Inicia el proceso de actualizacion de vinos.
     *
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
//      List<Bodega> sinBodega = new ArrayList<>();
//      pantallaActualizacionVino.mostrarbodegasActaulizables(sinBodega);

        pantallaActualizacionVino.mostrarBodegasActualizables(bodegaNombreBodegaMap.keySet());

    }

    /**
     * Obtiene la fecha y hora actual, y la setea al atributo 'fechaActual'
     * */
    private void getFechaActual() {
        fechaActual = LocalDateTime.now();
    }

    /**
     * Recibe la bodega seleccionada e inicia el proceso de actualizacion - creacion de vinos.
     * @param nombreBodegaSel type string
     */
    @Transactional
    public void tomarSeleccionBodegaPteActualizar(String nombreBodegaSel) {
//        obtengo el objeto del map.
        this.bodegaSeleccionada =  this.bodegaNombreBodegaMap.get(nombreBodegaSel);
//        hago disponible la bodega seleccionada en el manejador.
        this.bodegaSeleccionada = entityManager.merge(bodegaSeleccionada);
        obtenerActualizacionesBodegaSeleccionada();
    }

    // obtengo actualizacion de la api.
    private void obtenerActualizacionesBodegaSeleccionada() {
        Map<String, Object> respApiVinos = apiBodega.solicitarActualizacionVinos(bodegaSeleccionada);
        // Alternativa CU5 A3.
        if (respApiVinos.containsKey("error")) {
            pantallaActualizacionVino.SinRespuestaApi(respApiVinos.get("error").toString());
        } else {
//            casteo directamente porque el tipo que se espera recibir es una lista de vino.
                vinosRecibidos = (List<Vino>) respApiVinos.get("vinos");
            actualizarNovedadesVinos();
        }

    }

    /**
     * A partir de la lista de vinos de la bodega seleccionada brindada por el repositorio de JPA
     * obtengo una lista de vinos actualizables de la bodega.
     */
    private void actualizarNovedadesVinos() {
        vinosBodegaSeleccionada = iVinoRepository.findByBodega(bodegaSeleccionada);

//        vinoBodegaSeleccionadaList = new ArrayList<>(iVinoRepository.findAll()
//                .stream()
//                .filter(v -> v.sosDeEstaBodega(bodegaSeleccionada))
//                .toList());

//        Recorro los vinos recibidos para saber cual es actualizables.
        for (Vino vinoRecibido : vinosRecibidos) {
            if (bodegaSeleccionada.esTuVino(vinoRecibido, vinosBodegaSeleccionada)) {
                if (vinoActualizableList == null) vinoActualizableList = new ArrayList<>();
                vinoActualizableList.add(vinoRecibido);
            }
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

        for (Vino vinoRecibido : vinosRecibidos) {
            if (vinoActualizableList.contains(vinoRecibido)) {
                actualizarVinoExistente(vinoRecibido, vinosBodegaSeleccionada);
            } else {
                Vino v = crearVinoNuevo(vinoRecibido);
//               a los vinos de la bodega le agrego el vino creado.
                vinosBodegaSeleccionada.add(v);
                creados.add(v);
            }
        }
//seteo la ultima actualizacion de la bodega seleccionada.
        bodegaSeleccionada.setUltimaActualizacion(fechaActual.toLocalDate());
//guardo en la bd a travez de jpa.
        iVinoRepository.saveAll(vinosBodegaSeleccionada);

        ordenarInformacionAMostrar(creados);

    }

    /**
     * Actualiza los atributos actualizables de un vino; toma el vino recibido de la API
     * y los vinos de la bodega seleccionada y se los pasa por parametro a la bodega seleccionada
     * para que se actualicen los datos actualizables del vino.
     * SI BIEN EL GESTOR ES EL QUE TIENE LOS DATOS PARA ACTUALIZAR EL VINO EN CUESTION DELEGA ESTA
     * ACCION A LA BODEGA, ASI REDUCE ACOPLAMIENTO. PATRON EXPERTO EN INFORMACION.
     * @param vinoRecibido
     * @param vinoBodegaSeleccionadaList
     */
    private void actualizarVinoExistente(Vino vinoRecibido, List<Vino> vinoBodegaSeleccionadaList) {
        bodegaSeleccionada.actualizarDatosVino(vinoRecibido, vinoBodegaSeleccionadaList, fechaActual);
    }


    private void ordenarInformacionAMostrar(List<Vino> vinosCreados) {
        List<Vino> actualizados = vinosBodegaSeleccionada.stream().filter(v -> !vinosCreados.contains(v)).toList();
        pantallaActualizacionVino.mostrarResumenVinosimportados(actualizados, vinosCreados.size(), vinosCreados, vinosCreados.size(), bodegaSeleccionada.getNombre());
//        luego vamos a notificar a los enofilos.
        obtenerSeguidoresBodega(actualizados, vinosCreados.size(), vinosCreados, vinosCreados.size(), bodegaSeleccionada.getNombre());
    }


    /**
     * inica el proceso de notificacion a los enofilos suscriptos a la bodega que se realizo la actualizacion.
     * @param actualizados
     * @param size
     * @param vinosCreados
     * @param sized
     * @param nombre
     */
    private void obtenerSeguidoresBodega(List<Vino> actualizados, int size, List<Vino> vinosCreados, int sized, String nombre) {
        List<String> usuariosANotificar = iEnofiloRepository.findAll() //obtengo todos los enofilos.
                .stream() // Uso la libreria de stream para recorrer la lista de usuarios.
                .filter(e -> e.seguisABodega(bodegaSeleccionada)) // Filtro a los seguidores de las bodegas.
                .map(Enofilo::getNombreUsuario) // mapeo para obtener el nombre de usuario.
                .toList(); // convierto en lista.
        InterfazNotificacionPush.NotificarNovedadesAEnofiloSuscriptosBodega(actualizados, vinosCreados.size(), vinosCreados, vinosCreados.size(), bodegaSeleccionada.getNombre(), usuariosANotificar);

        finCU();

    }

    /**
     * Crea y retorna un Objeto Vino.
     * En este caso el gestor tiene todos los datos para crear el vino.
     * Aunque la creacion de los varietales se lo delega al vino; el gestor busca la informacion necesaria para crear los
     * varietales y se lo envia al vino para que este lo realice.
     * El vino ademas tiene  una relacion de 1..* esto significa que al crear el vino hay que crear y asignarle al menos un varietal.
     * Vemos el oatron controlador con el patron creador.
     * @param vinoRecibido
     * @return
     */

    private Vino crearVinoNuevo(Vino vinoRecibido) {
//        busca los tipos de uva.
        Map<TipoUva, Varietal> tipoUvaVarietalDTOMap = buscarTipoUva(vinoRecibido.getVarietales());

//        busca los maridaje
        List<Maridaje> maridaje = buscarMaridaje(vinoRecibido.getMaridajes());

        // Como en el diagram de clases hay una relacion de 0..* podriamos verificar si viene el maridaje
        // desde la bodega.

//        if(vinoRecibido.getMaridajes()!=null){
//            maridaje = buscarMaridaje(vinoRecibido.getMaridajes());
//        }

        Vino nuevoVino = new Vino(
                vinoRecibido.getAñada(),
                fechaActual,
                vinoRecibido.getImagenEtiqueta(),
                vinoRecibido.getNombre(),
                vinoRecibido.getNotaDeCataBodega(),
                vinoRecibido.getPrecioARS(),
                bodegaSeleccionada,
                maridaje);
//delego la creacion al vino creado a que cree sus varietales.
        nuevoVino.crearVarietal(tipoUvaVarietalDTOMap);
        return nuevoVino;
    }

    /**
     *  Toma los maridajes  del vino recibido, para obtenerlos y setearlos.
     *  Retorna una lista de maridajes.
     * No encuentra el maridaje lanza una excepcion, para el manejo de esta en las proximas versiones.
     * @param maridajeVinoRecibido
     * @return
     */

    private List<Maridaje> buscarMaridaje(List<Maridaje> maridajeVinoRecibido) {
        List<Maridaje> maridajes = new ArrayList<>();
        List<Maridaje> maridajesExistentes = iMaridajeRepository.findAll();

        for (Maridaje maridajeRecibido : maridajeVinoRecibido) {
            Maridaje m = maridajesExistentes
                    .stream()
                    .filter(maridaje -> maridaje.sosMaridaje(maridajeRecibido.getNombre()))
                    .findFirst().orElseThrow();
            maridajes.add(m);
//            for (Maridaje meridaje : maridajesExistentes) {
//                if (meridaje.sosMaridaje(maridajeRecibido.getNombre())) {
//                    maridajes.add(meridaje);
//                }
//            }
        }
        return maridajes;
    }

    /**
     * Toma los varietales del vino recibido, para obtener el tipo de uva. lo agrega a un
     * Map key:tipoUva, value: Varietal. Para luego retornarlo.
     * No encuentra el tipo uva lanza una excepcion, para el manejo de esta en las proximas versiones.
     * @param varietalRecibidoList
     * @return
     */
    private Map<TipoUva, Varietal> buscarTipoUva(List<Varietal> varietalRecibidoList) {
        Map<TipoUva, Varietal> tipoUvaVarietalDTOMap = new HashMap<>();

        List<TipoUva> tipoUvaList = iTipoUvaRepository.findAll();

//        por cada varietal recibido, busca el tipo de uva y lo agrega al map

        for (Varietal varietal : varietalRecibidoList) {
            TipoUva tipoUva = tipoUvaList
                    .stream()
                    .filter(tu -> tu.sosTipoUva(varietal.getTipoUva().getNombre()))
                    .findFirst()
                    .orElseThrow();

            tipoUvaVarietalDTOMap.put(tipoUva, varietal);
        }
        return tipoUvaVarietalDTOMap;
    }

private void finCU(){

}

}
