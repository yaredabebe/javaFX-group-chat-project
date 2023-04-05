package clientsocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class FXMLDocumentController {

@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text error;
    @FXML
    private Text userName;
    @FXML
    private AnchorPane homePage;

    @FXML
    private ListView<String> listView;

    @FXML
    private TextArea mesageTextArea;

    @FXML
    private AnchorPane messagePage;

    @FXML
    private TextField messageTextField;

    @FXML
    private TextField nametextField;

    @FXML
    private Text selectedListName;

    @FXML
    private Button sendButton;

    @FXML
    private Button sendNameButton;
    
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String sName;
    private String rName="all";
    private String message = null;

    @FXML
    void listViewClicked(MouseEvent event) {
        String selected=listView.getSelectionModel().getSelectedItems().toString();
        selectedListName.setText(selected.substring(selected.indexOf("[")+1,selected.indexOf("]")));
        rName=selected.substring(selected.indexOf("[")+1,selected.indexOf("]")-1);
        
    }
    

    @FXML
    void sendMessage(ActionEvent event) throws IOException {
        if(!messageTextField.getText().isEmpty()){
            bw.write( messageTextField.getText());
            bw.newLine();
            bw.write(rName);
            bw.newLine();
            bw.flush();
            mesageTextArea.appendText(messageTextField.getText()+"\n");
            messageTextField.setText("");
        }
    }
    @FXML
    void sendName(ActionEvent event) throws IOException {
        
        if(!nametextField.getText().isEmpty()){
            bw.write(nametextField.getText());
            bw.newLine();
            bw.flush();
            sName=nametextField.getText();
            nametextField.setText("");
            String validMassage;
            try {
                validMassage=br.readLine();
                System.out.println(validMassage);
                if(validMassage.toLowerCase().equals("valid")){
                    System.out.println("Valid");
                    homePage.setVisible(false);
                    messagePage.setVisible(true);
                    listView.getItems().add("all ");
                    selectedListName.setText("all");
                    mesageTextArea.setDisable(true);
                    userName.setText(sName);
                    while (true) {                        
                        String onlinNames=br.readLine();
                        if(onlinNames.toLowerCase().equals("end")){
                            break;
                        }
                        else{
                            listView.getItems().add(onlinNames+" ");
                        }
                    }
                    new Thread(new Runnable() {
			@Override
			public void run() {
				while(socket.isConnected()) {
					try {
						message=br.readLine();
                                                mesageTextArea.appendText(message+"\n");
                                                if(message.toLowerCase().contains("server")&&message.contains("has")){
                                                    System.out.println("aaaaaaaa");
                                                    Platform.runLater(new Runnable(){
                                                        @Override
                                                        public void run() {
                                                            addtoList();
                                                         }
                                                        
                                                    });
                                                    System.out.println("haaaaa");
                                                }
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println(e.getMessage());
					}
					
				}
			}

		} 
		).start();
                }else{
                    error.setVisible(true);
                    error.setText("you can not use this username");
                    System.out.println("InValid");
                }
            } catch (IOException e) {
            }
        }
    }

    private void addtoList() {
        listView.getItems().add(message.substring(message.indexOf(":")+2, message.indexOf("has")));
                                                
    }
    @FXML
    void initialize() throws IOException {
        
        socket = new Socket("localhost", 1234);
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
         
        
    }

}



