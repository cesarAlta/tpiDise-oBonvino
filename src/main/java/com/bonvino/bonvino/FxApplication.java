package com.bonvino.bonvino;

import com.bonvino.bonvino.Config.SpringFXMLLoader;
import com.bonvino.bonvino.SpringApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


//  Clase base de cualquier app JavaFX. Se usa para inicializar el contexto de Spring y lanzar la App JavaFX
public class FxApplication extends Application {
    private ConfigurableApplicationContext context;

    //    metodo que se llama antes que el start. Aqui inicia el contexto de Stpring utilizando  SpringApplicationBuilder
    //    y le paso por parametro la clase principal de springboot
    @Override
    public void init() {
        context = new SpringApplicationBuilder(SpringApp.class).run();
    }

    //    Metodo que se llama cuano javaFx esta listo. Publica un evento StageReadyEvent al contexto de spring, pasando el stage primario
    //    hace que los componentes de Sping puedan interactuar con el Stage principal.
    @Override
    public void start(Stage primaryStage) throws Exception {

        SpringFXMLLoader loader = context.getBean(SpringFXMLLoader.class);
        Parent root = loader.load("/index.fxml");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    //    Se llama cuando javaFX se cierra. Cierra el contexto de Soring y finaliza correctamente la app.
    @Override
    public void stop() {
        context.close();
    }

}
