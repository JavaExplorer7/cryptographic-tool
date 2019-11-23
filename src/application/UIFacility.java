package application;

import javafx.application.Platform;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class UIFacility {

  public static final String BG_STYLE = "-fx-background-color:black";

	public static Pane getExitSign() {
		Line lineLeft = new Line(0, 0, 30, 30);
		Line lineRight= new Line(30, 0, 0, 30);
		lineLeft.setStroke(Color.RED);
		lineRight.setStroke(Color.RED);

		StackPane pane = new StackPane(lineRight, lineLeft);
		pane.setOnMouseEntered(e -> {
			lineLeft.setStrokeWidth(lineLeft.getStrokeWidth() * 5);
			lineRight.setStrokeWidth(lineRight.getStrokeWidth() * 5);
		});
		pane.setOnMouseExited(e -> {
			lineLeft.setStrokeWidth(lineLeft.getStrokeWidth() / 5);
			lineRight.setStrokeWidth(lineRight.getStrokeWidth() / 5);
		});
		pane.setOnMouseClicked(e -> {
			Platform.exit();
			System.exit(0);
		});

		return pane;
	}

	public static Pane getMenu(String text) {
		Rectangle rect = new Rectangle(0, 0, Main.SCREEN_WIDTH / 3.5, Main.SCREEN_HEIGHT / 10);
		rect.setFill(Color.CORNFLOWERBLUE);

		Text txTitle = getText(text, Color.BLACK, 20.0);
		txTitle.setOnMouseEntered(e -> txTitle.setFont(Font.font(Font.getDefault().getFamily(),
				FontWeight.THIN, FontPosture.ITALIC, 22)));
		txTitle.setOnMouseExited(e -> txTitle.setFont(Font.font(Font.getDefault().getFamily(),
				FontWeight.THIN, FontPosture.ITALIC, 20)));

		return new StackPane(rect, txTitle);
	}

	public static Text getText(String text, Color color, double size) {
		Text txText = new Text(text);

		txText.setFill(color);
		txText.setEffect(new Lighting());
		txText.setBoundsType(TextBoundsType.LOGICAL);
		txText.setFont(Font.font(Font.getDefault().getFamily(),
				FontWeight.THIN, FontPosture.ITALIC, size));

		return txText;
	}

	public static RadioButton getRadioButton(String text) {
		RadioButton rb = new RadioButton(text);
		rb.setTextFill(Color.CORNFLOWERBLUE);
		rb.setFont(Font.font(Font.getDefault().getFamily(),
				FontWeight.BOLD, FontPosture.REGULAR, 16));

		return rb;
	}

}