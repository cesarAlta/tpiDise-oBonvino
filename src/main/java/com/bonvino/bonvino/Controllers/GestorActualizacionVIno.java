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
    PantallaActualizacionVino pantallaActualizacionVino;
    LocalDateTime fechaActual;

    public void OpionImportarActualizacionVino(PantallaActualizacionVino pantallaActualizacionVino) {
        this.pantallaActualizacionVino = pantallaActualizacionVino;
        buscarBodegasPtesActualizar();
    }

    private void getFechaActual() {
        fechaActual = LocalDateTime.now();
    }

    private void buscarBodegasPtesActualizar() {
        getFechaActual();

        List<Bodega> bodegaList = iBodegaRepository.findAll()
                .stream()
                .filter(bodega -> bodega.validarPeriodicidad(fechaActual.toLocalDate()))
                .toList();

//      List<Bodega> sinBodega = new ArrayList<>();
//      pantallaActualizacionVino.mostrarbodegasActaulizables(sinBodega);
        pantallaActualizacionVino.mostrarbodegasActaulizables(bodegaList);

    }

    private Bodega bodegaSeleccionada;

    @Transactional
    public void tomarSeleccionBodegaPteActualizar(Bodega bodegaSeleccionada) {
        this.bodegaSeleccionada = entityManager.merge(bodegaSeleccionada);
        obtenerActualizacionesBodegaSeleccionada();
    }

    //vinos recibidos desde la api
    List<Vino> vinoRecibidoList;
    //    vinos registrados de la bodega seleccionada.
    List<Vino> vinoBodegaSeleccionadaList;
    //    vinos para actualizar.
    List<Vino> vinoActualizableList;

    private void obtenerActualizacionesBodegaSeleccionada() {
        Map<String, Object> respApiVinos = apiBodega.solicitarActualizacionVinos(bodegaSeleccionada);
        // Alternativa CU5 A3.
        if (respApiVinos.containsKey("error")) {
            pantallaActualizacionVino.SinRespuestaApi(respApiVinos.get("error").toString());
        } else {
            vinoRecibidoList = (List<Vino>) respApiVinos.get("vinos");
            actualizarNovedadesVinos();
        }

    }


    private void actualizarNovedadesVinos() {

        vinoBodegaSeleccionadaList = new ArrayList<>(iVinoRepository.findAll()
                .stream()
                .filter(v -> v.sosDeEstaBodega(bodegaSeleccionada))
                .toList());

        for (Vino vinoRecibido : vinoRecibidoList) {
            if (bodegaSeleccionada.esTuVino(vinoRecibido, vinoBodegaSeleccionadaList)) {
                if (vinoActualizableList == null) vinoActualizableList = new ArrayList<>();
                vinoActualizableList.add(vinoRecibido);
            }

        }

        actualizarOCrearVinoBodega();
    }

    @Transactional
    private void actualizarOCrearVinoBodega() {
        List<Vino> creados = new ArrayList<>();

        for (Vino vinoRecibido : vinoRecibidoList) {
            if (vinoActualizableList.contains(vinoRecibido)) {
                actualizarVinoExistente(vinoRecibido, vinoBodegaSeleccionadaList);
            } else {
                Vino v = crearVinoNuevo(vinoRecibido);
                vinoBodegaSeleccionadaList.add(v);
                creados.add(v);
            }
        }

        bodegaSeleccionada.setUltimaActualizacion(fechaActual.toLocalDate());

        iVinoRepository.saveAll(vinoBodegaSeleccionadaList);

//        ordenarInformacionAMostrar(vinosCreados, vinosActualizados);

        ordenarInformacionAMostrar(creados);

    }

    private void ordenarInformacionAMostrar(List<Vino> vinosCreados) {
        List<Vino> actualizados = vinoBodegaSeleccionadaList.stream().filter(v -> !vinosCreados.contains(v)).toList();

        pantallaActualizacionVino.mostrarResumenVinosimportados(actualizados, vinosCreados.size(), vinosCreados, vinosCreados.size(), bodegaSeleccionada.getNombre());
        obtenerSeguidoresBodega(actualizados, vinosCreados.size(), vinosCreados, vinosCreados.size(), bodegaSeleccionada.getNombre());
    }

    private void obtenerSeguidoresBodega(List<Vino> actualizados, int size, List<Vino> vinosCreados, int sized, String nombre) {
       List<String> usuariosANotificar = iEnofiloRepository.findAll() //obtengo todos los ususarios.
               .stream() // Uso la libreria de stream para recorrer la lista de usuarios.
               .filter(e->e.seguisABodega(bodegaSeleccionada)) // Filtro a los seguidores de las bodegas.
               .map(Enofilo::getNombreUsuario) // mapeo para obtener el nombre de usuario.
               .toList(); // convierto en lista.
        InterfazNotificacionPush.NotificarNovedadesAEnofiloSuscriptosBodega(actualizados, vinosCreados.size(), vinosCreados, vinosCreados.size(), bodegaSeleccionada.getNombre(), usuariosANotificar);

    }

    private void actualizarVinoExistente(Vino vinoRecibido, List<Vino> vinoBodegaSeleccionadaList) {
        bodegaSeleccionada.actualizarDatosVino(vinoRecibido, vinoBodegaSeleccionadaList, fechaActual);
    }

    private Vino crearVinoNuevo(Vino vinoRecibido) {

        Map<TipoUva, Varietal> tipoUvaVarietalDTOMap = buscarTipoUva(vinoRecibido.getVarietales());
        List<Maridaje> maridaje = buscarMaridaje(vinoRecibido.getMaridajes());

        // Como en el diagram de clases hay una relacion de 0..* podriamos verificar si viene el varidaje
        // desde la bodega.

//        if(vinoRecibido.getMaridajes()!=null){
//            maridaje = buscarMaridaje(vinoRecibido.getMaridajes());
//        }
        return new Vino(
                vinoRecibido.getAñada(),
                fechaActual,
                vinoRecibido.getImagenEtiqueta(),
                vinoRecibido.getNombre(),
                vinoRecibido.getNotaDeCataBodega(),
                vinoRecibido.getPrecioARS(),
                bodegaSeleccionada,
                maridaje,
                tipoUvaVarietalDTOMap);
    }


    private List<Maridaje> buscarMaridaje(List<Maridaje> maridajeVinoRecibido) {
        List<Maridaje> maridajes = new ArrayList<>();

        for (Maridaje maridajeRecibido : maridajeVinoRecibido) {
            for (Maridaje meridaje : iMaridajeRepository.findAll()) {
                if (meridaje.sosMaridaje(maridajeRecibido.getNombre())) {
                    maridajes.add(meridaje);
                }
            }
        }
        return maridajes;
    }

    private Map<TipoUva, Varietal> buscarTipoUva(List<Varietal> varietalRecibidoList) {
        Map<TipoUva, Varietal> tipoUvaVarietalDTOMap = new HashMap<>();
        List<TipoUva> tipoUvaList = iTipoUvaRepository.findAll();

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


}
