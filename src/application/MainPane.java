package application;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import api.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainPane extends BorderPane {

	private String filename = "/"; // file to encrypt or decrypt
  private String ksFilepath = "/";// file to open as a key store

  private Text txState;  // user hint text
  private Text txFilename;
  private Text txKSFile;
  private TextField tfLoadedKey;
  private TextField tfAlias;
  private ComboBox<String> cboKeys;
  private ComboBox<String> cboAlgo;
  private PasswordField pfPassword;
  private static CryptKeyStore keyStore;

  public MainPane() {
    this.setTop(initMenuBar());
    this.setCenter(initWelcome());
  }

  private Pane initWelcome() {
    Text txGreeting = UIFacility.getText(
        "Cryptographic Tool", Color.ALICEBLUE, 30.0);

    StackPane pane = new StackPane(txGreeting);
    pane.setPadding(new Insets(Main.SCREEN_HEIGHT / 3));

    return pane;
  }


  private HBox initMenuBar() {
    Pane muHash = UIFacility.getMenu("Hash/Digest");
    Pane muCiph = UIFacility.getMenu("Encrypt/Decrypt");
    Pane muKS  = UIFacility.getMenu("Key Store");

    muHash.setOnMouseClicked(e -> this.setCenter(new HashPane()));
    muCiph.setOnMouseClicked(e -> setCipherPane());
    muKS.setOnMouseClicked(e -> setKSPane());

    HBox box = new HBox(15.0);
    box.setStyle(UIFacility.BG_STYLE);
    box.getChildren().addAll(muHash, muCiph, muKS, UIFacility.getExitSign());
    return box;
  }


  private void setCipherPane() {
    txState = UIFacility.getText("\nHint: To encrypt a file, go to Key Store tab "
    		+ "and load your store, \n then come back here", Color.RED, 15.0);

    GridPane pane = new GridPane();
    pane.setHgap(20.0);
    pane.setVgap(20.0);
    pane.setPadding(new Insets(Main.SCREEN_HEIGHT / 15, 0, 0, 0));
    pane.setAlignment(Pos.CENTER);
    pane.setStyle(UIFacility.BG_STYLE);

    this.setCenter(pane);
    this.setBottom(txState);

    // show nothing except hint if key store have not loaded
    // or there is no key in this store
    if (keyStore == null)
    	return;
    if (cboKeys.getItems().isEmpty()) {
    	txState.setText("\nThere is no key in this store, try to add some");
    	return;
    }

    Text txChooseKey = UIFacility.getText("1. Choose Key:", Color.CORNFLOWERBLUE, 18.0);

    Text txChooseAlgo = UIFacility.getText("2. Pick Algorithm:", Color.CORNFLOWERBLUE, 18.0);
    cboAlgo = new ComboBox<>();
    cboAlgo.getItems().addAll(Utility.getAlgorithms());
    cboAlgo.getSelectionModel().selectFirst();

    Text txChooseMode = UIFacility.getText("3. Mode:", Color.CORNFLOWERBLUE, 18.0);
    ToggleGroup group = new ToggleGroup();
    RadioButton rbEncrypt = UIFacility.getRadioButton("Encryption");
    RadioButton rbDecrypt = UIFacility.getRadioButton("Decryption");
    rbEncrypt.setToggleGroup(group);
    rbDecrypt.setToggleGroup(group);
    rbEncrypt.setSelected(true);

    HBox modeBox = new HBox(20.0);
    modeBox.setStyle(UIFacility.BG_STYLE);
    modeBox.getChildren().addAll(rbEncrypt, rbDecrypt);

    txFilename = UIFacility.getText("/" + new File(filename).getName(), Color.CORNFLOWERBLUE, 18.0);
    Button btChoose = new Button("4. Select File");
    btChoose.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
      File file = fileChooser.showOpenDialog(new Stage());
      if (file != null) {
        filename = file.getAbsolutePath();
        txFilename.setText(file.getName());
      }
    });

    Button btGo = new Button("Go!");
    btGo.setOnAction(e -> {
    	String alias = cboKeys.getSelectionModel().getSelectedItem();
    	SecretKey key = keyStore.getKey(alias);

    	String algo = cboAlgo.getSelectionModel().getSelectedItem();

    	String outFilename = filename + ".";
    	if (rbEncrypt.isSelected()) {
    		outFilename += "encrypted." + algo.replaceAll("/", "-");
    		if (CryptCipher.encrypt(filename, outFilename, algo, key))
    			txState.setText("\nEncryped");
    		else
    			txState.setText("\nMake sure your key is appropriate for algorithm");
    	} else {
    		outFilename += "decrypted." + algo.replaceAll("/", "-");
    		if (CryptCipher.decrypt(filename, outFilename, algo, key))
    			txState.setText("\nDecrypted");
    		else
    			txState.setText("\nMake sure your key is appropriate for algorithm");
    	}
    });

    pane.add(txChooseKey, 0, 0);
    pane.add(txChooseAlgo, 0, 1);
    pane.add(txChooseMode, 0, 2);
    pane.add(btChoose, 0, 3);

    pane.add(cboKeys, 1, 0);
    pane.add(cboAlgo, 1, 1);
    pane.add(modeBox, 1, 2);
    pane.add(txFilename, 1, 3);
    pane.add(btGo, 1, 4);

    txState.setText("\nMake sure your key is appropriate for algorithm.\n"
    		+ "Resulting filename will have the form of\n\t "
    		+ "filename.encrypted/decrypted.algorithm");
  }

  private void setKSPane() {
    Text txStore = UIFacility.getText("Current Store", Color.CORNFLOWERBLUE, 18.0);
    txKSFile = UIFacility.getText("/" + new File(ksFilepath).getName(), Color.CORNFLOWERBLUE, 18.0);

    Text txPassword = UIFacility.getText("Password", Color.CORNFLOWERBLUE, 18.0);
    pfPassword = new PasswordField();
    pfPassword.setPromptText("unlock store");

    Button btChoose = new Button("Choose Store");
    btChoose.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
      File file = fileChooser.showOpenDialog(new Stage());
      if (file != null) {
        ksFilepath = file.getAbsolutePath();
        txKSFile.setText(file.getName());
      }
    });

    Button btCreateStore = new Button("Create Store");
    btCreateStore.setOnAction(e -> {
      File file = new File(ksFilepath);
      if (file.isFile() && file.canWrite() && pfPassword.getText().length() > 0) {
        keyStore = CryptKeyStore
            .createKeyStore(ksFilepath, pfPassword.getText());
        txState.setText("\nKey Store \"" + txKSFile.getText() + "\" created, ready to use");
      } else {
        txState.setText("\nCheck the path or password (not null)");
      }
    });

    tfLoadedKey = new TextField("");
    tfLoadedKey.setPrefWidth(30.0);
    tfLoadedKey.setEditable(false);
    if (cboKeys == null)
    	cboKeys = new ComboBox<>();
    cboKeys.setOnAction(e -> {
    	tfLoadedKey.clear();
      String alias = cboKeys.getSelectionModel().getSelectedItem();
      if (alias != null && alias.length() > 0)
      	tfLoadedKey.setText(Utility.bytesToHex(keyStore.getKey(alias).getEncoded()));
    });
    Button btLoad = new Button("Load");
    btLoad.setOnAction(e -> {
    	if (ksFilepath.length() < 1 || pfPassword.getText().length() < 1)
    		txState.setText("\nCheck the store path or password (not null)");
    	else {
        keyStore = new CryptKeyStore(ksFilepath, pfPassword.getText());
        if (keyStore != null && keyStore.isOK()) {
          cboKeys.getItems().clear();
          cboKeys.getItems().addAll(keyStore.getAliases());
          txState.setText("\nStore loaded");
        }
        else
          txState.setText("\nCheck the store path or password (not null)");
    	}
    });

    Button btDelete = new Button("Delete Key");
    btDelete.setOnAction(e -> {
      if (cboKeys.getItems().size() > 0) {
        String alias = cboKeys.getSelectionModel().getSelectedItem();
        if (keyStore != null && keyStore.deleteKey(alias)) {
          cboKeys.getItems().remove(alias);
          cboKeys.getSelectionModel().clearAndSelect(0);
          tfLoadedKey.clear();
          txState.setText("\nKey \"" + alias + "\" deleted");
        }
        else
          txState.setText("\nCheck your store");
      }
      else
        txState.setText("\nNothing here");
    });


    Text txAlias = UIFacility.getText("Alias", Color.CORNFLOWERBLUE, 18.0);
    Text txType = UIFacility.getText("Type", Color.CORNFLOWERBLUE, 18.0);
    Text txKeyLengthLabel = UIFacility.getText("Length(Bits)", Color.CORNFLOWERBLUE, 18.0);
    Text txKeyLength = UIFacility.getText("", Color.CORNFLOWERBLUE, 18.0);

    tfAlias = new TextField("");
    tfAlias.setPrefWidth(30.0);
    ComboBox<String> cboTypes = new ComboBox<>();
    cboTypes.getItems().addAll(Utility.KEY_TYPES.keySet());
    cboTypes.getSelectionModel().selectFirst();
    txKeyLength.setText(Utility.KEY_TYPES.get(cboTypes.getSelectionModel().getSelectedItem()));

    cboTypes.setOnAction(e -> {
    	String type = cboTypes.getSelectionModel().getSelectedItem();
    	txKeyLength.setText(Utility.KEY_TYPES.get(type));
    });

    Button btCreate = new Button("Create Secret Key");
    btCreate.setOnAction(e -> {
      if (pfPassword.getText().length() < 1 || tfAlias.getText().length() < 1)
        txState.setText("\nPassword and alias can not be empty");
      else if (ksFilepath.length() < 1 || keyStore == null)
      	txState.setText("\nHaven't load the your store yet!");
      else {
        String type = cboTypes.getSelectionModel().getSelectedItem();
        try {
          SecretKey sKey = KeyGenerator.getInstance(type).generateKey();
          if (keyStore.addKey(tfAlias.getText(), sKey))
            txState.setText("\nKey \"" + tfAlias.getText() + "\" added, reload to see it");
          else
            txState.setText("\nProblem with Secret Key Generation");
        } catch (NoSuchAlgorithmException e1) {
          e1.printStackTrace();
          txState.setText("\nProblem with Secret Key Generation");
        }
      }
    });

    txState = UIFacility.getText("\nHint: To create a new key store, "
        + "choose a new file as location, \ntype in new password and "
        + "click Create Store", Color.RED, 15.0);

    GridPane pane = new GridPane();
    pane.setHgap(20.0);
    pane.setVgap(20.0);
    pane.setPadding(new Insets(Main.SCREEN_HEIGHT / 15, 0, 0, 0));
    pane.setAlignment(Pos.CENTER);
    pane.setStyle(UIFacility.BG_STYLE);

    pane.add(btChoose, 0, 0);
    pane.add(txStore, 1, 0);
    pane.add(txKSFile, 2, 0);

    pane.add(btCreateStore, 0, 1);
    pane.add(txPassword, 1, 1);
    pane.add(pfPassword, 2, 1);
    pane.add(btLoad, 3, 1);

    pane.add(cboKeys, 1, 2);
    pane.add(tfLoadedKey, 2, 2);
    pane.add(btDelete, 3, 2);

    pane.add(txAlias, 1, 3);
    pane.add(txType, 2, 3);
    pane.add(txKeyLengthLabel, 3, 3);
    pane.add(btCreate, 0, 4);
    pane.add(tfAlias, 1, 4);
    pane.add(cboTypes, 2, 4);
    pane.add(txKeyLength, 3, 4);

    this.setCenter(pane);
    this.setBottom(txState);
  }

}
