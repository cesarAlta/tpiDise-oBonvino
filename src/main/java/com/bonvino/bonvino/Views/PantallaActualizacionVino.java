package com.bonvino.bonvino.Views;

import com.bonvino.bonvino.Config.SpringFXMLLoader;
import com.bonvino.bonvino.Controllers.GestorActualizacionVIno;
import com.bonvino.bonvino.DTOs.VinoInfoDTO;
import com.bonvino.bonvino.Models.Bodega;
import com.bonvino.bonvino.Models.Varietal;
import com.bonvino.bonvino.Models.Vino;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Component
public class PantallaActualizacionVino {

    String bodegaSeleccionada;
    //    Inyecto el gestorActualizacionVino
    @Autowired
    GestorActualizacionVIno gestorActualizacionVino;
    //    Inyecto el manejador de ventanas
    @Autowired
    SpringFXMLLoader springFXMLLoader;
//    ...

    //    Atributos de la pantalla
    @FXML
    Button btnActualizarBodega;
    @FXML
    Button btnCancelarActualizacionBodegas;
    @FXML
    Pane paneSeleccionBodega;
//    ...

    //  Error mensaje sin seleccion de bodega
    @FXML
    AnchorPane errorNoSeleccionBodega;
    @FXML
    Button btnCerrarAviso;
    //    ...
//    Pane vinos actualizados y creados
    @FXML
    Label textBodegaSeleccionada;
    @FXML
    VBox bodegasDisponiblesVB;
    @FXML
    Button btnTerminar;
    //...
//    Error sin bodegas disponibles para actualizar
    @FXML
    AnchorPane errorSinBodegaDisponible;
    @FXML
    Button btnCerrarAvisoSinBodegasDisponible;
    //    ...
    //Atributos mensaje error API
    @FXML
    Button btnCerrarErrorApi;
    @FXML
    Text textErrorApi;
    @FXML
    AnchorPane errorAniSinResp;
    @FXML
    AnchorPane vinoContainer;
    @FXML
    AnchorPane scrollPane;
    @FXML
    Label labelError;


    //habilitar pantalla
    @FXML
    public void initialize() {

        gestorActualizacionVino.opcionImportarActualizacionVinos(this);

//      asigno estilos a lo botones

        btnCancelarActualizacionBodegas.getStyleClass().add("btn-cancel");
        btnCerrarAviso.getStyleClass().add("btn-cancel");
        btnActualizarBodega.getStyleClass().add("btn-ok");
        btnTerminar.getStyleClass().add("btn-ok");
        btnCerrarAvisoSinBodegasDisponible.getStyleClass().add("btn-cancel");
        btnCerrarErrorApi.getStyleClass().add("btn-cancel");


    }

    /**
     * Genera un Hbox con estilos  para mostra una lista con nombres de las bodegas recibidas.
     * En caso de no haber bodegas muestra un mensaje y una opción para cerrar la ventana.     *
     *
     * @param bodegaList Set String con nombres de bodegas.
     */

    public void mostrarBodegasActualizables(Set<String> bodegaList) {
        if (bodegaList.isEmpty()) {
            errorSinBodegaDisponible.setVisible(true);
            paneSeleccionBodega.setVisible(false);

            btnCerrarAvisoSinBodegasDisponible.setOnAction(e -> {
                try {
                    irPantallaPrincipal();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

        } else {
            bodegasDisponiblesVB.setSpacing(10);
// por cada bodega genra un HBox y da estilos y se lo agrega a un VBox para ser mostrado
            for (String bodegaNombre : bodegaList) {
                HBox bodegaHB = new HBox();
                bodegaHB.getStyleClass().add("card-bodega");
                Label l = new Label();
                l.setText(bodegaNombre);

                bodegaHB.setOnMouseClicked(
                        e -> {
                            if (bodegaHB.getStyleClass().contains("select")) {
                                bodegaHB.getStyleClass().remove("select");
                                bodegaSeleccionada = null;
                            } else {
                                bodegaSeleccionada = bodegaNombre;
                                bodegaHB.getStyleClass().add("select");
                            }
                        }
                );

                bodegaHB.getChildren().addAll(l);
                bodegasDisponiblesVB.getChildren().add(bodegaHB);
            }
        }

    }

    //    Envia al gestor la bodega seleccionada. En caso de que no se sellecione ninguna muestra un aviso.
    @FXML
    public void tomarSeleccionBodegaPteActualizar() {
        if (bodegaSeleccionada == null) {
            errorNoSeleccionBodega.setVisible(true);
        } else {
            gestorActualizacionVino.tomarSeleccionBodegaPteActualizar(bodegaSeleccionada);
        }

    }


    //    public void mostrarResumenVinosimportados(List<Vino> actualizados, int cantActualizados, List<Vino> vinosCreados, int cantCreados, String nombreBodega) {
//
//
//        VBox vinoVBox = new VBox(10); // 10px spacing between cards
//        vinoVBox.setPadding(new Insets(20));
//        vinoContainer.setVisible(true);
//        textBodegaSeleccionada.setText("Bodega " + nombreBodega);
//
//        for (Vino v : actualizados) {
//            HBox card = createVinoCard(v, "act");
//            vinoVBox.getChildren().add(card);
//        }
//
//        scrollPane.setPrefHeight(155 * actualizados.size());
//        scrollPane.getChildren().add(vinoVBox);
//
//        paneSeleccionBodega.visibleProperty().setValue(false);
//
//    }
    public void mostrarResumenVinosimportados(List<VinoInfoDTO> vinoList, String nombreBodega) {
        VBox vinoVBox = new VBox(10); // 10px spacing between cards
        vinoVBox.setPadding(new Insets(20));
        vinoContainer.setVisible(true);
        textBodegaSeleccionada.setText("Bodega " + nombreBodega);

        for (VinoInfoDTO v : vinoList) {
            HBox card = createVinoCard(v);
            vinoVBox.getChildren().add(card);
        }

//        scrollPane.setPrefHeight(155 * actualizados.size());
        scrollPane.getChildren().add(vinoVBox);

        paneSeleccionBodega.visibleProperty().setValue(false);


    }

    private HBox createVinoCard(VinoInfoDTO v) {
        HBox tarjetaVino = new HBox(10);
        tarjetaVino.setPrefWidth(500);
        tarjetaVino.setPrefHeight(150);
        tarjetaVino.getStyleClass().add("card");
//imagen
        ImageView image = new ImageView();
        image.setImage(new Image(v.getImagenEtiqueta()));
        image.setFitWidth(150);
        image.setFitHeight(120);

        VBox card = new VBox(5);
        card.setPrefWidth(300);
        card.setPrefHeight(100);

        Label title = new Label(v.getNombreYañada());
        title.getStyleClass().add("card-title");

//        Nota de cata
        Label notaCata = new Label("Nota de cata de bodega +");
        notaCata.getStyleClass().add("item-plus");

        VBox notaCataVBox = new VBox();
        Text notaCataBodega = new Text(v.getNotaCataDeBodega());
        notaCataVBox.getStyleClass().add("info-vino");
        notaCataBodega.setWrappingWidth(320);
        notaCataVBox.getChildren().add(notaCataBodega);

        notaCata.setOnMouseClicked(
                e -> {
                    if (card.getChildren().contains(notaCataVBox)) {
                        card.getChildren().remove(notaCataVBox);
                        notaCata.getStyleClass().remove("item-select");
                        notaCata.setText("Nota de cata de bodega +");


                    } else {
                        card.getChildren().add(card.getChildren().indexOf(notaCata) + 1, notaCataVBox);
                        notaCata.getStyleClass().add("item-select");

                        notaCata.setText("Nota de cata de bodega -");
                    }
                }
        );

        Label varietalPrincipal = new Label(v.getVarietalPrincipal() + " +");
        varietalPrincipal.getStyleClass().add("item-plus");


        VBox vBoxVarietales = new VBox();
        vBoxVarietales.getStyleClass().add("info-vino");
        for (String varietalInfo : v.getVarietales()) {
            Label l = new Label(varietalInfo);
            vBoxVarietales.getChildren().add(l);
        }

        varietalPrincipal.setOnMouseClicked(
                e -> {
                    if (card.getChildren().contains(vBoxVarietales)) {
                        card.getChildren().remove(vBoxVarietales);
                        varietalPrincipal.getStyleClass().remove("item-select");
                        varietalPrincipal.setText(v.getVarietalPrincipal() + " +");


                    } else {
                        card.getChildren().add(2, vBoxVarietales);
                        varietalPrincipal.getStyleClass().add("item-select");
                        varietalPrincipal.setText(v.getVarietalPrincipal() + " -");
                    }
                }
        );


        VBox vBoxMaridaje = new VBox();
        vBoxMaridaje.getStyleClass().add("info-vino");

        for (String maridajesInfo : v.getMaridajes()) {
            Text textMaridaje = new Text(maridajesInfo);
            textMaridaje.setWrappingWidth(320);
            vBoxMaridaje.getChildren().add(textMaridaje);
        }

        Label maridajeLabel = new Label("Maridajes +");
        maridajeLabel.getStyleClass().add("item-plus");

        maridajeLabel.setOnMouseClicked(
                e -> {
                    if (card.getChildren().contains(vBoxMaridaje)) {
                        card.getChildren().remove(vBoxMaridaje);
                        maridajeLabel.getStyleClass().remove("item-select");
                        maridajeLabel.setText("Maridajes +");


                    } else {
                        card.getChildren().add(card.getChildren().indexOf(maridajeLabel) + 1, vBoxMaridaje);
                        maridajeLabel.getStyleClass().add("item-select");
                        maridajeLabel.setText("Maridajes -");
                    }
                }
        );


        Label precioLbl = new Label("AR$ " + v.getPrecio());
        precioLbl.getStyleClass().add("card-description");

        Label etiqueta = new Label(v.getImagenEtiqueta());
        etiqueta.getStyleClass().add("card-description");


        card.getChildren().addAll(title, varietalPrincipal, maridajeLabel, notaCata, precioLbl);


        Label nuevoOActualizado = new Label();
        nuevoOActualizado.setText(v.getFechaActualizacion() == null ? "Nuevo ingreso" : "Actualizado: " + v.getFechaActualizacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        nuevoOActualizado.getStyleClass().add("rounded-pill");
        nuevoOActualizado.getStyleClass().add(v.getFechaActualizacion() == null ? "nuevo" : "actualizado");
        card.getChildren().add(nuevoOActualizado);


        tarjetaVino.getChildren().addAll(image, card);


        return tarjetaVino;
    }

    @FXML
    public void onbtnCerrarAvisoNoSelecBodega() {
        bodegaSeleccionada = null;
        errorNoSeleccionBodega.setVisible(false);
//        paneSeleccionBodega.setVisible(true);
    }

    @FXML
    public void onCancelar() throws IOException {
        irPantallaPrincipal();

    }

    @FXML
    public void onFinalizar() throws IOException {
        irPantallaPrincipal();


    }

    public void irPantallaPrincipal() throws IOException {
        Stage currentStage = (Stage) btnTerminar.getScene().getWindow();
        Parent root = springFXMLLoader.load("/index.fxml");

        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.show();

        currentStage.close();
    }


    //    Error respuesta api. Muestra el mensaje de error.
//    Alternativa CU5 A3.
    public void SinRespuestaApi(String error) {
        errorAniSinResp.setVisible(true);
        paneSeleccionBodega.setVisible(false);
        textErrorApi.setText(error);
        textErrorApi.setTextAlignment(TextAlignment.CENTER);
        btnCerrarErrorApi.setOnMouseClicked(e -> {
            try {
                irPantallaPrincipal();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }
}
