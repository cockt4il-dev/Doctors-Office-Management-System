package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Controller {
	@FXML
	private TextField tfTitle;
	private Stage stage;
	private Scene scene;
	private Parent root;

	@FXML
	public void btnOKClicked(ActionEvent event) {
		Stage mainWindow = (Stage) tfTitle.getScene().getWindow();
		String title = tfTitle.getText();
		mainWindow.setTitle(title);

	}

	public void switchToScene1(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("ProjectDesign.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public void switchToScene2(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);

		stage.centerOnScreen();

		stage.show();
	}

	@FXML
	private CheckBox login_checkbox;

	@FXML
	private TextField login_username;

	@FXML
	private AnchorPane login_form;

	@FXML
	private Button login_login;

	@FXML
	private PasswordField login_password;

	@FXML
	private Hyperlink login_registerHere;

	@FXML
	private CheckBox register_checkbox;

	@FXML
	private TextField register_email;

	// @FXML
	// private TextField register_fn;

	@FXML
	private AnchorPane register_form;

	@FXML
	private TextField register_username;

	@FXML
	private Hyperlink register_loginHere;

	@FXML
	private PasswordField register_password;
	
	@FXML
	private TextField register_showPassword;

	@FXML
	private PasswordField register_password_confirm;

	@FXML
	private Button register_signup;
	
	@FXML
	private TextField login_showPassword;

	// DB TOOLS

	private Connection connect;
	private PreparedStatement prepare;
	private ResultSet result;
	private AlertMessage alert = new AlertMessage();
	
	public void loginAccount() {
	    if (login_username.getText().isEmpty() || login_password.getText().isEmpty()) {
	        alert.errorMessage("Incorrect Username/Password");
	    } else {
	        String username = login_username.getText();
	        String password = login_password.getText();
	        
	        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
	        
	        connect = Database.connectDB();
	        
	        try {
	            if (login_showPassword.isVisible()) {
	                // Use the visible password field
	                password = login_showPassword.getText();
	            }
	            
	            prepare = connect.prepareStatement(sql);
	            prepare.setString(1, username);
	            prepare.setString(2, password);
	            result = prepare.executeQuery();
	            
	            if (result.next()) {
	                // Correct
	                alert.successMessage("Login Successfully");
	            } else {
	                // Wrong
	                alert.errorMessage("Incorrect Username/Password");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            // Close resources
	            try {
	                if (result != null) result.close();
	                if (prepare != null) prepare.close();
	                if (connect != null) connect.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

	
public void loginShowPassword() {
		
		
		if(login_checkbox.isSelected()) {
			login_showPassword.setText(login_password.getText());
			login_showPassword.setVisible(true);
			login_password.setVisible(false);
		}else {
			login_password.setText(login_showPassword.getText());
			login_showPassword.setVisible(false);
			login_password.setVisible(true);
		}
		
		
	}

	public void registerAccount() {
		if (register_email.getText().isEmpty() || register_username.getText().isEmpty()
				|| register_password.getText().isEmpty() || register_password_confirm.getText().isEmpty()) {
			alert.errorMessage("All blank fields must be filled");
		} else {
			String checkUsername = "SELECT * FROM admin WHERE username = ?";
			connect = Database.connectDB();
			try {
				
			if(!register_showPassword.isVisible()) {
				if(!register_showPassword.getText().equals(register_password.getText())) {
					register_showPassword.setText(register_password.getText());
				}
				
			}
			else {
				if(!register_showPassword.getText().equals(register_password.getText())) {
					register_password.setText(register_password.getText());
				}
			}
			
				prepare = connect.prepareStatement(checkUsername);
				prepare.setString(1, register_username.getText());
				result = prepare.executeQuery();
				if (result.next()) {
					alert.errorMessage(register_username.getText() + " Already exists!");
					//CHECK PASSWORD LENGTH
				} else if (register_password.getText().length() < 8) { 

					alert.errorMessage("Invalid Password, at least 8 characters needed.");
				}

				else {
					String insertData = "INSERT INTO admin (email, username, password, date) VALUES (?, ?, ?, ?)";

					java.time.LocalDate currentDate = java.time.LocalDate.now();
					java.sql.Date sqlDate = java.sql.Date.valueOf(currentDate);

					prepare = connect.prepareStatement(insertData);
					prepare.setString(1, register_email.getText());
					prepare.setString(2, register_username.getText());
					prepare.setString(3, register_password.getText());
					prepare.setDate(4, sqlDate); // Use setDate() for SQL Date type
					prepare.executeUpdate();
					alert.successMessage("Registered Successfully");
					
					//switch to login after success
					
					login_form.setVisible(true);
					register_form.setVisible(false);
					
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
public void registerShowPassword() {
		
		
		if(register_checkbox.isSelected()) {
			register_showPassword.setText(register_password.getText());
			register_showPassword.setVisible(true);
			register_password.setVisible(false);
		}else {
			register_password.setText(register_showPassword.getText());
			register_showPassword.setVisible(false);
			register_password.setVisible(true);
		}
		
		
	}

	
	public void switchForm(ActionEvent event) {

		if (event.getSource() == login_registerHere) {
			login_form.setVisible(false);
			register_form.setVisible(true);
		} else if (event.getSource() == register_loginHere) {
			login_form.setVisible(true);
			register_form.setVisible(false);

		}

	}

}