package com.bonvino.bonvino.Views;

import com.bonvino.bonvino.Config.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IndexController {
    @Autowired
    SpringFXMLLoader springFXMLLoader;

    @FXML
    Button btnImportarVinos;
    @FXML
    public void opcionImportarVino() throws IOException {
        Stage stage = (Stage) btnImportarVinos.getScene().getWindow(); //obtengo el stage actual
        Parent root = springFXMLLoader.load("/PantallaActualizacionVino.fxml");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
