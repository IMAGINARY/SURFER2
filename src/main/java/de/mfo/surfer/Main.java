package de.mfo.surfer;
 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.Node;
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
import javafx.scene.image.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import jfxtras.labs.scene.layout.ScalableContentPane;
 
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.nio.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.text.Position;

import com.sun.pdfview.*;
import com.sun.pdfview.action.GoToAction;
import com.sun.pdfview.action.PDFAction;
import java.lang.reflect.InvocationTargetException;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Main extends Application {
    
    private Scene scene;
    
    private Node createBrowser()
    {
        WebView browser = new WebView();
        browser.setFontSmoothingType( FontSmoothingType.GRAY );
        browser.getEngine().load( "http://www.mathjax.org/demos/tex-samples/" );

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter( browser );

        return borderPane;
    }

    private Node createPDFViewer()
    {
        URL pdfURL;
        try
        {
            pdfURL = this.getClass().getResource("geometry.pdf");
        }
        catch( Exception e )
        {
            pdfURL = null;
        }
        
        PDFImageView pdfIV = null;
        try
        {
            pdfIV = new PDFImageView( pdfURL );
        }
        catch( IOException ioe )
        {
            System.err.println( ioe );
            pdfIV = null;
        }

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter( pdfIV );

        return borderPane;
    }

    private Node createScalableLayout()
    {
        Font.loadFont( this.getClass().getResource("NimbusSanL-Regu-Surfer.ttf").toExternalForm(), 12.0 );
        
        final ScalableContentPane scaledPane = new ScalableContentPane();

        Button btn = new Button();
        btn.setText( "CSS" );
        btn.setOnAction(new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent event) {
                System.err.println("Adding stylesheet");

                String cssUrl = this.getClass().getResource( "surfer.css" ).toExternalForm();
                scene.getStylesheets().removeAll( cssUrl );
                scene.getStylesheets().add( cssUrl );
            }
        });
        
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
        
        buttons.prefWidthProperty().bind( scaledPane.widthProperty() );

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

        return scaledPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) { 
        primaryStage.setTitle( "SURFER 2 Tests" );   

        TabPane tabPane = new TabPane();
        Tab tab;

        tab = new Tab();
        tab.setText( "Web browser" );
        tab.setContent( createBrowser() );
        tabPane.getTabs().add(tab);

        tab = new Tab();
        tab.setText( "PDF viewer" );
        tab.setContent( createPDFViewer() );
        tabPane.getTabs().add(tab);

        tab = new Tab();
        tab.setText( "Scalable layout" );
        tab.setContent( createScalableLayout() );
        tabPane.getTabs().add(tab);

        scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene( scene );
        primaryStage.show();
    }
}

class PDFStuff
{
    public static PDFFile openFile(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        InputStream istr = urlConnection.getInputStream();
        byte[] byteBuf = new byte[ 10 * 1024 * 1024 ]; //10 MB
        int offset = 0;
        int read = 0;
        while (read >= 0) {
            read = istr.read(byteBuf, offset, 1024 );
            if (read > 0) {
                offset += read;
            }
        }
        ByteBuffer buf = ByteBuffer.allocate( offset );
        buf.put(byteBuf,0,offset);
        
        return new PDFFile( buf );
    }
    
    public static BufferedImage renderPDF( URL url, String outlinePos, int width, int height )
    {
        try
        {
            /*
            URLConnection connection = url.openConnection();
            // Since you get a URLConnection, use it to get the InputStream
            InputStream is = connection.getInputStream();
            // Now that the InputStream is open, get the content length
            int contentLength = connection.getContentLength();

            // create temporary buffer
            ByteArrayOutputStream tmpOut = new ByteArrayOutputStream( contentLength == -1 ? 16384 : contentLength );

            // fill temporary buffer
            byte[] buf = new byte[512];
            int len;
            while( ( len = is.read(buf) ) != -1 )
                tmpOut.write(buf, 0, len);

            // close buffers
            is.close();
            tmpOut.close();

            // create ByteBuffer, which we need for the PDFRenderer
            ByteBuffer byte_buf = ByteBuffer.wrap( tmpOut.toByteArray() );

            // render PDF into image
            PDFFile pdfFile = new PDFFile( byte_buf );
            */
            PDFFile pdfFile = openFile( url );
            System.err.println( pdfFile.getVersionString() );
            PDFPage pdfPage = pdfFile.getPage( 0 );
            if( outlinePos != null )
            {
                OutlineNode outline = pdfFile.getOutline();
                JTree jt = new JTree( outline );
                TreePath tp = jt.getNextMatch( outlinePos, 0, Position.Bias.Forward );
                
                if( tp != null )
                {
                    OutlineNode node = (OutlineNode) tp.getLastPathComponent();
                    if( node != null )
                    {
//                        try {
                            PDFAction action = node.getAction();
                            if (action != null && action instanceof GoToAction )
                            {
                                PDFDestination dest = ((GoToAction) action).getDestination();
                                if (dest != null && dest.getPage() != null )
                                {
                                    pdfPage = pdfFile.getPage( pdfFile.getPageNumber( dest.getPage() ) + 1 );
                                }                       
                            }
                            /*
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }     
                        * */
                    }
                }
            }
            
            return renderPage( pdfPage, width, height );
        }
        catch( Exception e )
        {
            System.err.println( e );
        }
        return null;
    }
    
    public static BufferedImage renderPage( PDFPage pdfPage, int width, int height )
    {
        BufferedImage bImg = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        java.awt.geom.Rectangle2D r2d = pdfPage.getBBox();
        java.awt.Image img = pdfPage.getImage( width, height, r2d, null, true, true );
        bImg.getGraphics().drawImage( img, 0, 0, null );
        return bImg;
    }
}

class PDFImageView extends Region
{
    private PDFFile pdfFile;
    private PDFPage pdfPage;
    private ImageView iv;
    private Service pdfRenderService;
    
    public PDFImageView( URL url ) throws IOException
    {
        pdfFile = PDFStuff.openFile( url );
        pdfPage = pdfFile.getPage( 0 );
        //this.setFitWidth( 400 );
        //this.setFitHeight( 300 );
        iv = new ImageView();
        iv.setSmooth( true );
                
        ChangeListener cl = new ChangeListener() {

            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                PDFImageView.this.updatePDFImage();
            }
        };
        
        this.widthProperty().addListener( cl );
        this.heightProperty().addListener( cl );
        
        this.getChildren().add( iv );
        
        this.pdfRenderService = new Service< Image >() {
            @Override
            protected Task<Image> createTask() {
                return new Task<Image>() {

                    @Override
                    protected Image call() {
                        int w = (int) PDFImageView.this.getWidth();
                        int h = (int) PDFImageView.this.getHeight();
                        return SwingFXUtils.toFXImage( PDFStuff.renderPage( pdfPage, 2 * w, 2 * h ), null );
                    }
                };
            }
            
            @Override
            protected void succeeded() {
                iv.setImage( getValue() );
            }
        };
    }

    public void gotoPage( int pageNum )
    {
        pdfPage = pdfFile.getPage( pageNum );
        updatePDFImage();
    }
    
    protected void updatePDFImage()
    {
        final int w = (int) this.getWidth();
        final int h = (int) this.getHeight();                
        if( w > 0 && h > 0 )
        {
            iv.setFitWidth( w );
            iv.setFitHeight( h );
            this.pdfRenderService.restart();
        }
    }
}
