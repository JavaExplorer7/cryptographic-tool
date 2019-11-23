package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
	
	public static double SCREEN_WIDTH  = 600.0;
	public static double SCREEN_HEIGHT = SCREEN_WIDTH / 16 * 9;
	
	//variables for storing initial position of the stage at the beginning of drag
	private double initX;
	private double initY;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// INITIALISATION OF THE STAGE/SCREEN
		
		//create stage which has set stage style transparent
		final Stage stage = new Stage(StageStyle.TRANSPARENT);
		//create root node of scene, i.e. group
		Group rootGroup = new Group();
		//create scene with set width, height and color
		Scene scene = new Scene(rootGroup, SCREEN_WIDTH, SCREEN_HEIGHT, Color.TRANSPARENT);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		scene.setCursor(Cursor.HAND);
		//set scene to stage
		stage.setScene(scene);
		//center stage on screen
		stage.centerOnScreen();
		//set stage full screen
		//stage.setFullScreen(true);
		//show the stage
		stage.show();

		// CREATION OF THE DRAGGER
		 
		//create dragger with desired size
		Rectangle dragger = new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT);
		//fill the dragger with some nice radial background
		dragger.setFill(Color.BLACK);

		//when mouse button is pressed, save the initial position of screen
		rootGroup.setOnMousePressed(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent me) {
						initX = me.getScreenX() - stage.getX();
						initY = me.getScreenY() - stage.getY();
					}
				});

		//when screen is dragged, translate it accordingly
		rootGroup.setOnMouseDragged(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent me) {
						stage.setX(me.getScreenX() - initX);
						stage.setY(me.getScreenY() - initY);
					}
				});
		
		//press Q/f to terminate the application
		rootGroup.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (null != event.getCode())
					switch (event.getCode()) {
					case Q:
						Platform.exit();
						System.exit(0);						
					default:
						break;
				}
			}
		});

		MainPane pane = new MainPane();
		pane.setTranslateY(SCREEN_HEIGHT / 10);
		//add all nodes to main root group
		rootGroup.getChildren().addAll(dragger, pane);	
		rootGroup.requestFocus();
	}
	
	public static void main(String[] args) {
		//Get primary screen bounds
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		
		SCREEN_WIDTH = screenBounds.getWidth() / 2;
		SCREEN_HEIGHT= screenBounds.getHeight() / 2;

		launch(args);
	}
	
}
