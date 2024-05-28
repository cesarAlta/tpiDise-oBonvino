package com.bonvino.bonvino.Views;

import com.bonvino.bonvino.Config.SpringFXMLLoader;
import com.bonvino.bonvino.Controllers.GestorActualizacionVIno;
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

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class PantallaActualizacionVino {

    Bodega bodegaSeleccionada;
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
    public void initialize() {

        gestorActualizacionVino.OpionImportarActualizacionVino(this);

//      asiglo estilos a lo botones

        btnCancelarActualizacionBodegas.getStyleClass().add("btn-cancel");
        btnCerrarAviso.getStyleClass().add("btn-cancel");
        btnActualizarBodega.getStyleClass().add("btn-ok");
        btnTerminar.getStyleClass().add("btn-ok");
        btnCerrarAvisoSinBodegasDisponible.getStyleClass().add("btn-cancel");
        btnCerrarErrorApi.getStyleClass().add("btn-cancel");


    }

    public void mostrarbodegasActaulizables(List<Bodega> bodegaList) {
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

            for (Bodega bodega : bodegaList) {
                HBox bodegaHB = new HBox();
                bodegaHB.getStyleClass().add("card-bodega");
                Label l = new Label();
                l.setText(bodega.getNombre());
                bodegaHB.setOnMouseClicked(
                        e -> {
                            if (bodegaHB.getStyleClass().contains("select")) {
                                bodegaHB.getStyleClass().remove("select");
                                bodegaSeleccionada = null;
                            } else {
                                bodegaSeleccionada = bodega;
                                bodegaHB.getStyleClass().add("select");
                            }
                        }
                );

                bodegaHB.getChildren().addAll(l);
                bodegasDisponiblesVB.getChildren().add(bodegaHB);
            }
        }


    }

    public void tomarSeleccionBodegaPteActualizar() {
        if (bodegaSeleccionada == null) {
            errorNoSeleccionBodega.setVisible(true);
        } else {
            gestorActualizacionVino.tomarSeleccionBodegaPteActualizar(bodegaSeleccionada);
        }

    }

    @FXML
    AnchorPane vinoContainer;
    @FXML
    AnchorPane scrollPane;
    @FXML
    Label labelError;

    public void mostrarResumenVinosimportados(List<Vino> actualizados, int cantActualizados, List<Vino> vinosCreados, int cantCreados, String nombreBodega) {


        VBox vinoVBox = new VBox(10); // 10px spacing between cards
        vinoVBox.setPadding(new Insets(20));
        vinoContainer.setVisible(true);
        textBodegaSeleccionada.setText("Bodega " + nombreBodega);

        for (Vino v : actualizados) {
            HBox card = createVinoCard(v, "act");
            vinoVBox.getChildren().add(card);
        }

        for (Vino v : vinosCreados) {
            HBox card = createVinoCard(v, "nuevo");
            vinoVBox.getChildren().add(card);
        }
        scrollPane.setPrefHeight(155 * actualizados.size());
        scrollPane.getChildren().add(vinoVBox);

        paneSeleccionBodega.visibleProperty().setValue(false);


    }

    private HBox createVinoCard(Vino v, String n) {
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

        Label title = new Label(v.getNombre() + " - " + v.getAñada());
        title.getStyleClass().add("card-title");

        Label notaCata = new Label(v.getNotaDeCataBodega());
        notaCata.getStyleClass().add("card-description");

        Optional<Varietal> varietalOptional = v.getVarietales()
                .stream()
                .max(Comparator.comparingDouble(Varietal::getPorcentajeComposicion));

        String varietal;
        if (varietalOptional.isPresent()) {
            varietal = varietalOptional.get().getTipoUva().getNombre();
        } else {
            varietal = "No se puedo cargar el varietal";
        }


        Label varietalPrincipal = new Label(varietal + " +");
        varietalPrincipal.getStyleClass().add("item-plus");


        VBox vBoxVarietales = new VBox();
        vBoxVarietales.getStyleClass().add("info-vino");
        for (String s : v.getVarietales().stream().map(var -> var.getDescripcion() + ": " + var.getPorcentajeComposicion() + "%").toList()) {
            Label l = new Label(s);
            vBoxVarietales.getChildren().add(l);
        }

        varietalPrincipal.setOnMouseClicked(
                e -> {
                    if (card.getChildren().contains(vBoxVarietales)) {
                        card.getChildren().remove(vBoxVarietales);
                        varietalPrincipal.getStyleClass().remove("item-select");
                        varietalPrincipal.setText(varietal + " +");


                    } else {
                        card.getChildren().add(2, vBoxVarietales);
                        varietalPrincipal.getStyleClass().add("item-select");
                        varietalPrincipal.setText(varietal + " -");
                    }
                }
        );


        VBox vBoxMaridaje = new VBox();
        vBoxMaridaje.getStyleClass().add("info-vino");

        for (String s : v.getMaridajes().stream().map(var -> var.getNombre() + ": " + var.getDescripcion()).toList()) {
            Text textMaridaje = new Text(s);
            textMaridaje.setWrappingWidth(350);
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


        Label precioLbl = new Label("AR$ " + v.getPrecioARS());
        precioLbl.getStyleClass().add("card-description");

        Label etiqueta = new Label(v.getImagenEtiqueta());
        etiqueta.getStyleClass().add("card-description");

        card.getChildren().addAll(title, varietalPrincipal, maridajeLabel, precioLbl);


        Label l = new Label();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String fechaAct = v.getFechaActualizacion().format(formatter);


        l.setText(n.equals("nuevo") ? "Nuevo ingreso" : "Actualizado: " + fechaAct);
        l.getStyleClass().add("rounded-pill");
        l.getStyleClass().add(n.equals("nuevo") ? "nuevo" : "actualizado");
        card.getChildren().add(l);


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
