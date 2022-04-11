package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private MeteoDAO dao;
	private List<Citta> parziale;
	private List<Citta> migliore;
	private List<Citta> allCities;
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		this.dao = new MeteoDAO();
	}
	
	public List<Citta> getAllCitta(){
		//return this.dao.getAllCitta();
		this.allCities = this.dao.getAllCitta();
		return this.allCities;
	}
	public List<Rilevamento> getAllRilevamenti(){
		return this.dao.getAllRilevamenti();
	}
	
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita){
		return this.dao.getAllRilevamentiLocalitaMese(mese, localita);
	}

	// of course you can change the String output with what you think works best
	public double getUmiditaMedia(int mese, Citta c) {
		return this.dao.getUmiditaMediaMeseCitta(mese, c);
	}
	
	/*
	 * Alternativa valida, ma meglio far fare queste operazioni al DAO:
	 * public Map<Citta, Double> getUmiditaMedia(int mese) {
		Map<Citta, Double> umiditaMediaCitta = new HashMap<Citta, Double>();
		List<Rilevamento> temp;
		int somma;
		int numeroRilevamenti;
		double umiditaMedia;
		for(Citta c : this.getAllCitta()) {
			temp = this.getAllRilevamentiLocalitaMese(mese, c.getNome());
			somma = 0;
			numeroRilevamenti = 0;
			umiditaMedia = 0.0;
			for(Rilevamento r : temp) {
				numeroRilevamenti++;
				somma += r.getUmidita();
			}
			umiditaMedia = somma / numeroRilevamenti;
			umiditaMediaCitta.put(c, umiditaMedia);
		}
		return umiditaMediaCitta;
	}
	 */
	
	// of course you can change the String output with what you think works best
	public List<Citta> trovaSequenza(int mese) {
		//inizializzo soluzione parziale e migliore
		parziale = new ArrayList<Citta>();
		this.migliore = null;
		//carico per ogni citta tutti i rilevamenti
		for(Citta c : this.allCities) {
			c.setRilevamenti(this.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		//inizializzo metodo ricorsivo
		this.sequenzaRicorsiva(0, parziale);
		return migliore;
	}
	

	private void sequenzaRicorsiva(int livello, List<Citta> parziale){
		//caso terminale
		if(livello == NUMERO_GIORNI_TOTALI ) {
			double costo = this.calcolaCosto(parziale);
			//devo verificare di non aver già trovato la soluzione migliore 
			//oppure che il costo della soluzione parziale sia migliore di quello della soluzione migliore
			if(this.migliore == null || costo < calcolaCosto(migliore))
			this.migliore = new ArrayList<Citta>(parziale);
		}
		//caso normale
		if(livello < NUMERO_GIORNI_TOTALI) {
			for(Citta c : this.allCities) {
				if(vincoliOK(c, parziale)) {
					//Aggiongo alle soluzioni
					parziale.add(c);
					//ricorsione
					sequenzaRicorsiva(livello+1, parziale);
					//backtracking
					parziale.remove(parziale.size()-1);
				}
			}
		}
	}
	
	private boolean vincoliOK(Citta c, List<Citta> parziale) {
		//caso 1: non ho ancora niente in parziale
		if(parziale.size() == 0)
			return true; //è lecito aggiungere la città perchè tanto non ce n'è nessuna inserita
		//caso 2: siamo al secondo o al terzo giorno ---> non posso cambiare citta: 
		// verifico se quella che voglio provare ad aggiungere è quella gia presente o meno
		if(parziale.size() == 1 || parziale.size() == 2) {
			return parziale.get(parziale.size()-1).equals(c);
			//ritorna true se c è uguale a quella già in parziale, altrimenti false
		}
		//caso 3: sono oltre i 3 giorni ---> non devono essercenen più di 6
		int count =  0;
		for(Citta cc : parziale) {
			if(cc.equals(c)) {
				count++;
			}
		}
		if(count >= NUMERO_GIORNI_CITTA_MAX)
			return false;
		//per i giorni successivi a 3, non oltre i sei posso rimanere con la stessa citta
		if(parziale.get(parziale.size()-1).equals(c)) {
			return true;
		}
		//per cambiare città, devo essere rimasto nella stessa nei tre gg precedenti
		if(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) && parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3))) {
			return true; //posso cambiare
		}
		else {
			return false;
		}
	}

	private double calcolaCosto(List<Citta> lista) {
		double costo = 0.0;
		for(int g = 1; g <= NUMERO_GIORNI_TOTALI; g++) {
			//citta attuale
			Citta c = lista.get(g-1);
			//prendo l'umidità di quel giorno
			double umid = c.getRilevamenti().get(g-1).getUmidita();
			//aggiungo al costo
			costo += umid;
		}
		//per ogni cambio città aggiungo 100 euro al costo
		for(int g = 2; g <= NUMERO_GIORNI_TOTALI; g++) {
			Citta cc = lista.get(g-1);
			if(!cc.equals(lista.get(g-2))) {
				costo += COST;
			}
		}
		return costo;
	}
}
