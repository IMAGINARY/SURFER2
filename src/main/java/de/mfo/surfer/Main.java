package de.mfo.surfer;
 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.transform.Scale;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.beans.value.*;
import javafx.event.EventType;
import javafx.scene.text.FontSmoothingType;

import jfxtras.labs.scene.layout.ScalableContentPane;
 
public class Main extends Application {
    
    private Scene scene;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        Font.loadFont( this.getClass().getResource("NimbusSanL-Regu-Surfer.ttf").toExternalForm(), 12.0 );
        
        primaryStage.setTitle( "SURFER 2" );
        Button btn = new Button();
        btn.setText( "CSS" );
        btn.setOnAction(new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent event) {
                System.err.println("Adding stylesheet");
                String cssUrl = "file:/media/DATA/IMAGINARY/SURFER2/src/main/resources/de/mfo/surfer/surfer.css";
                System.out.println( scene.getStylesheets().size() );
                scene.getStylesheets().removeAll( cssUrl );
                scene.getStylesheets().add( cssUrl );
                System.out.println( scene.getStylesheets().size() );
                //scene.getStylesheets().add( this.getClass().getResource( "surfer.css" ).toExternalForm() );
            }
        });
        
        final ScalableContentPane scaledPane = new ScalableContentPane();
        
        scaledPane.setStyle("-fx-background-color: green;");
        Pane root = scaledPane.getContentPane();
        root.setStyle("-fx-background-color: blue;");
        
        BorderPane borderPane = new BorderPane();
//        borderPane.setTop(hbox);
//        borderPane.setLeft(addVBox());
//        borderPane.setCenter(addGridPane());
//        borderPane.setRight(addFlowPane());
        
        VBox formulaAndButtons = new VBox();
        TextField tf = new TextField();
        tf.setStyle( "-fx-font-size: 30;" );
        formulaAndButtons.getChildren().add( tf );

        final FlowPane buttons = new FlowPane();
        buttons.setStyle("-fx-background-color: red;");
        //formulaAndButtons.getChildren().add( buttons );
        buttons.getChildren().add( btn );
        
        char[] btnTexts = { 'x', 'y', 'z', '+', '-', '*', '²', '³', 'n', 'a', 'b', 'c', 'd', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ',', '(', ')' };
        for( char c : btnTexts )
        {
            Button b = new Button( "" + c );
//            b.getTransforms().add( new Scale( 2.0, 2.0 ) );
            buttons.getChildren().add( b );
        }
        
        //borderPane.setBottom( formulaAndButtons );
        
        //root.getChildren().add(borderPane);
        root.getChildren().add(buttons);
        
        scaledPane.widthProperty().addListener(
            new ChangeListener(){
                @Override public void changed(ObservableValue o,Object oldVal, Object newVal)
                {
                    scaledPane.layout();
                    scaledPane.getContentPane().layout();
                }
            }
        );

        WebView browser = new WebView();
        browser.setFontSmoothingType( FontSmoothingType.GRAY );
        browser.getEngine().load( "file:///home/stussak/Downloads/pdf2svgtest.svg" );
        
        
        buttons.prefWidthProperty().bind( scaledPane.widthProperty() );
        
        //scene = new Scene(scaledPane, 300, 200);
        scene = new Scene(browser, 800, 600);
        primaryStage.setScene( scene );
        scene.getStylesheets().add( this.getClass().getResource( "surfer.css" ).toExternalForm() );
        primaryStage.show();
    }
}
