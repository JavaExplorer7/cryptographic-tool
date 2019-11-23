package application;

import java.io.File;

import api.CryptDigest;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HashPane extends GridPane {

  private String filepath = "/";  // file to do hash

  private Text txState;  // user hint text
  private Text txFile;
  private TextField tfHash;
  private TextField tfGivenHash;

	HashPane() {
    txFile  = UIFacility.getText("/", Color.CORNFLOWERBLUE, 18.0);
    Button btLoad = new Button("Load a File");
    btLoad.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
      File file = fileChooser.showOpenDialog(new Stage());
      if (file != null) {
        filepath = file.getAbsolutePath();
        txFile.setText("/" + file.getName());
      }
    });

    Text txAlgo = UIFacility.getText("Hash Function", Color.CORNFLOWERBLUE, 18.0);
    ComboBox<String> cboAlgo = new ComboBox<>();
    cboAlgo.getItems().addAll(Utility.HASH_FUNCTION_NAMES);
    cboAlgo.setPromptText("Pick one");
    cboAlgo.setOnAction(e -> {
      String algo = cboAlgo.getSelectionModel().getSelectedItem();
      File file = new File(filepath);
      if (file.isFile() && Utility.HASH_FUNCTION_NAMES.contains(algo))
        tfHash.setText(CryptDigest.computeDigest(filepath, algo));
    });

    Text txHash = UIFacility.getText("Computed Hash", Color.CORNFLOWERBLUE, 18.0);
    tfHash = new TextField("...");
    tfHash.setEditable(false);
    tfHash.setPrefWidth(30.0);

    Text txGivenHash = UIFacility.getText("Given Hash", Color.CORNFLOWERBLUE, 18.0);
    tfGivenHash = new TextField("");
    tfGivenHash.setPrefWidth(30.0);

    Button btCompare = new Button("Same?");
    btCompare.setOnAction(e -> {
      if (tfHash.getText().compareToIgnoreCase(tfGivenHash.getText()) == 0) {
      	txState.setText("Yep.");
      	txState.setFill(Color.LAWNGREEN);
      }
      else {
      	txState.setText("Nope.");
      	txState.setFill(Color.RED);
      }
    });

    txState = UIFacility.getText("Waiting...", Color.CORNFLOWERBLUE, 25.0);

    this.setHgap(20.0);
    this.setVgap(20.0);
    this.setPadding(new Insets(Main.SCREEN_HEIGHT / 8, 0, 0, 0));
    this.setAlignment(Pos.CENTER);
    this.setStyle(UIFacility.BG_STYLE);

    this.add(btLoad, 0, 0);
    this.add(txFile, 1, 0);
    this.add(txAlgo, 0, 1);
    this.add(cboAlgo, 1, 1);
    this.add(txHash, 0, 2);
    this.add(tfHash, 1, 2);
    this.add(txGivenHash, 0, 3);
    this.add(tfGivenHash, 1, 3);
    this.add(btCompare, 1, 4);
    this.add(txState, 0, 5);
	}

}