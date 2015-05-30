package com.scapelog.client.ui;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.scapelog.api.util.Components;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.config.Config;
import com.scapelog.client.model.User;
import com.scapelog.client.ui.component.WindowControls;
import com.scapelog.client.ui.util.CSS;
import com.scapelog.client.util.RSA;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;

public final class LoginWindow extends Application {
	private final String sectionName = "login";
	private final String usernameConfig = "username";
	private final String rememberConfig = "remember_me";

	private double toolbarOffsetX = 0;
	private double toolbarOffsetY = 0;

	private final ScapeLog scapeLog;

	private TextField usernameField, passwordField;
	private CheckBox rememberMe;
	private Button login;

	public LoginWindow(ScapeLog scapeLog) {
		this.scapeLog = scapeLog;
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Login :: ScapeLog");
		stage.initStyle(StageStyle.UNDECORATED);

		GridPane root = new GridPane();
		root.setId("root");

		Scene scene = new Scene(root, 400, 350);
		setupUI(root, scene, stage);

		CSS.addDefaultStyles(scene.getStylesheets());
		CSS.addStylesheets(LoginWindow.class, scene.getStylesheets(), "/css/loginwindow.css");

		setDraggable(stage, scene);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}

	private void setupUI(GridPane root, Scene scene, Stage stage) {
		HBox controlsBox = new HBox(
				Components.createSpacer(),
				new WindowControls(stage)
		);

		/* Logo */
		ImageView img = new ImageView(new Image(LoginWindow.class.getResourceAsStream("/img/logo.png")));
		img.setId("logo");
		img.setPreserveRatio(true);
		BorderPane logoPane = new BorderPane(img);
		logoPane.setPrefWidth(scene.getWidth());

		/* Response label */
		Label responseLabel = new Label("");
		responseLabel.setMaxWidth(Double.MAX_VALUE);
		responseLabel.setId("response-label");

		/* Username field */
		usernameField = new TextField();
		Label usernameLabel = new Label("Username");
		usernameLabel.setLabelFor(usernameField);
		VBox usernameFieldBox = new VBox(5,
			usernameLabel, usernameField
		);
		usernameFieldBox.setId("username-box");
		usernameField.setText(Config.getString(sectionName, usernameConfig, ""));

		/* Password field */
		passwordField = new PasswordField();
		Label passwordLabel = new Label("Password");
		passwordLabel.setLabelFor(passwordField);
		VBox passwordFieldBox = new VBox(5,
			passwordLabel, passwordField
		);
		passwordFieldBox.setId("password-box");

		/* Remember me */
		rememberMe = new CheckBox("Remember me");
		rememberMe.setMaxHeight(Double.MAX_VALUE);
		rememberMe.setSelected(Config.getBoolean(sectionName, rememberConfig, false));

		/* Login button */
		login = Components.createBorderedButton("Login");
		login.setId("login-button");
		HBox loginBox = new HBox(
			rememberMe, Components.createSpacer(), login
		);
		loginBox.setId("login-box");

		/* Links */
		VBox accountLinks = new VBox(
			createHyperlink("Register", "https://forums.scapelog.com/member.php?action=register", "left"),
			createHyperlink("Recover password", "https://forums.scapelog.com/member.php?action=lostpw", "left")
		);
		VBox otherLinks = new VBox(
			createHyperlink("Website", "https://www.scapelog.com/", "right"),
			createHyperlink("Forums", "https://forums.scapelog.com/", "right")
		);
		HBox linkBox = new HBox(
			accountLinks,
				Components.createSpacer(),
				otherLinks
		);
		linkBox.setPadding(new Insets(3, 10, 0, 10));
		linkBox.setMaxWidth(Double.MAX_VALUE);

		int row = 0;
		root.add(controlsBox, 0, row++);
		root.add(logoPane, 0, row++);
		root.add(responseLabel, 0, row++);
		root.add(usernameFieldBox, 0, row++);
		root.add(passwordFieldBox, 0, row++);
		root.add(loginBox, 0, row++);
		root.add(linkBox, 0, row);

		if (!usernameField.getText().isEmpty()) {
			passwordField.requestFocus();
		}

		EventHandler<ActionEvent> loginEvent = (event) -> {
			if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
				fail(responseLabel, "Enter your username and password");
				return;
			}

			lockComponents(true);
			responseLabel.getStyleClass().remove("fail");
			responseLabel.setText("Logging in...");

			String loc = "https://api.scapelog.com";
			/*if (true) {
				loc = "http://localhost:9013";
			}*/
			final String location = loc;

			ScapeLog.getExecutor().submit(() -> {
				try {
					Webb webb = Webb.create();
					webb.setBaseUri(location);
					webb.setFollowRedirects(true);
					webb.setDefaultHeader(Webb.HDR_USER_AGENT, "scapelog/1.0");

					Response<JSONObject> response = webb
							.post("/auth")
							.param(RSA.encrypt("username"), RSA.encrypt(usernameField.getText()))
							.param(RSA.encrypt("password"), RSA.encrypt(passwordField.getText()))
							.connectTimeout(5000)
							.readTimeout(5000)
							.asJsonObject();

					JSONObject result = response.getBody();
					if (response.getStatusCode() == 200 && result != null) {
						boolean success = false;
						String message = "Something went wrong, please try again soon.";
						String token = null;
						if (result.has("success")) {
							success = result.getBoolean("success");
						}
						if (result.has("message")) {
							message = result.getString("message");
						}
						if (result.has("token")) {
							token = result.getString("token");
						}

						String groups = "";
						if (result.has("groups")) {
							groups = result.getString("groups");
						}

						lockComponents(false);

						if (success && token != null) {
							Config.setBoolean(sectionName, rememberConfig, rememberMe.isSelected());
							Config.setString("login", "username", usernameField.getText());

							ScapeLog.setUser(new User(usernameField.getText(), token, groups));
							Platform.runLater(() -> {
								scapeLog.startUI();
								stage.close();
							});
						} else {
							fail(responseLabel, message);
						}
					}
				} catch (Exception e) {
					fail(responseLabel, "Couldn't connect to ScapeLog, please try again soon.");
					lockComponents(false);
					//e.printStackTrace();
				}
			});
		};
		login.setOnAction(loginEvent);
		usernameField.setOnAction((e) -> passwordField.requestFocus());
		passwordField.setOnAction((e) -> {
			if (usernameField.getText().isEmpty()) {
				usernameField.requestFocus();
				return;
			}
			loginEvent.handle(e);
		});
	}

	private void fail(Label label, String text) {
		Platform.runLater(() -> {
			label.getStyleClass().add("fail");
			label.setText(text);
		});
	}

	private void lockComponents(final boolean lock) {
		Platform.runLater(() -> {
			usernameField.setDisable(lock);
			passwordField.setDisable(lock);
			login.setDisable(lock);
			usernameField.requestFocus();
		});
	}

	private Hyperlink createHyperlink(String text, String url, String styleClass) {
		Hyperlink hyperlink = new Hyperlink(text);
		hyperlink.setId("link");
		hyperlink.getStyleClass().add(styleClass);
		hyperlink.setMaxWidth(Double.MAX_VALUE);
		hyperlink.setOnAction((e) -> getHostServices().showDocument(url));
		return hyperlink;
	}

	private void setDraggable(Stage stage, Scene scene) {
		scene.setOnMousePressed(e -> {
			toolbarOffsetX = e.getSceneX();
			toolbarOffsetY = e.getSceneY();
		});
		scene.setOnMouseDragged(e -> {
			if (e.isPrimaryButtonDown()) {
				int x = (int) (e.getScreenX() - toolbarOffsetX);
				int y = (int) (e.getScreenY() - toolbarOffsetY);
				stage.setX(x);
				stage.setY(y);
			}
		});
	}

}