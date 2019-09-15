package siso.exams.salatv;

public abstract class Sala {

	//valori costanti, numero canali e numero massimo di persone in sala
	final int CANALI = 8, MAX_PERSONE = 30;
	//Lista persone che vogliono guardare un determinato canale
	int[] richiesta = new int[CANALI];
	//contatore persone nella stanza
	int numPersone;
	//Canale attuale 
	int canaleAttuale = -1;
	
	/*
	 * Gestisce l'ingresso di una persona nella Sala
	 */
	abstract void entra(int c) throws InterruptedException;
	
	
	/*
	 * Gestisce l'uscita di una persona dalla Sala
	 */
	abstract void esci(int c)throws InterruptedException;
	
	
	/*
	 *  Calcola il canale piu' richiesto in sala 
	 */
	int canaleRichiesto() {
		int i_max = 0;
		for (int i = 0; i < richiesta.length; i++) 
			if(richiesta[i] > richiesta[i_max]) 
				i_max = i;
		return i_max;
	}
	
	/*
	 * 
	 */
	void simula(int persone) {
		for(int i = 0 ; i < persone; i++)
			new Persona(this).start();
	}
		
	
}//Sala
