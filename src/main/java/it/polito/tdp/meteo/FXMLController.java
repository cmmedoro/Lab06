/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.meteo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxMese"
    private ChoiceBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnUmidita"
    private Button btnUmidita; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalcola"
    private Button btnCalcola; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaSequenza(ActionEvent event) {
    	this.reset();
    	Integer mese = this.boxMese.getValue();
    	if(mese == null) {
    		this.txtResult.setText("DEVI SCEGLIERE UN MESE!");
    		return;
    	}
    	this.txtResult.appendText("La sequenza ottima per il mese scelto e': \n");
    	List<Citta> sequenza = this.model.trovaSequenza(mese);
    	for(Citta c : sequenza) {
    		this.txtResult.appendText(c.getNome()+"\n");
    	}
    }

    @FXML
    void doCalcolaUmidita(ActionEvent event) {
    	this.reset();
    	Integer mese = this.boxMese.getValue();
    	if(mese == null) {
    		this.txtResult.setText("DEVI SCEGLIERE UN MESE!");
    		return;
    	}
    	for(Citta c : this.model.getAllCitta()) {
    		double uMedia = this.model.getUmiditaMedia(mese, c);
    		this.txtResult.appendText(c.getNome()+": "+uMedia+"\n");
    	}
    	
    }
    
    private void reset() {
    	this.txtResult.clear();
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	//popolo la choiceBox
    	for(int i = 1; i <= 12; i++) {
    		this.boxMese.getItems().add(i);
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
}

