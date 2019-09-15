package siso.exams.piscina;

import java.util.LinkedList;
import java.util.Random;

public abstract class Piscina {

	//
	public final static int CORSIE = 5, MAX_NUOTATORI = 4;
	//booleana che determina se la piscina è aperta o meno
	protected boolean piscinaAperta;
	//Lista di nuotatori nelle corsie
	int[] corsie;
	//Numero totale di nuotatori attuali da interrompere in caso di chiusura
	LinkedList<Thread> nuotatori;
	
	
	
	public Piscina() {
		piscinaAperta = false;
		corsie = new int[CORSIE];
		nuotatori = new LinkedList<>();
	}
	
	/*
	 * Metodo che gestisce l'ingresso di un nuotatore nella piscina
	 * la scelta della corsia e l'ingresso nella stessa
	 */
	public abstract int entra()throws InterruptedException;
	
	/*
	 * Gestione dell'uscita di un nuotatore dalla corsia e dell'abbandono
	 * della piscina
	 */
	public abstract void esci(int corsia)throws InterruptedException;
	
	/*
	 * Sfruttato dall'istruttore per aprire la piscina ai nuotatori
	 */
	public abstract void apriPiscina()throws InterruptedException;
	
	/*
	 * Sfruttato dall'istruttore per chiudere la piscina, interrompere quindi i nuotatori
	 * e sgomberare le file
	 */
	public abstract void chiudiPiscina()throws InterruptedException;
	
	/*
	 * Gestione della scelta della corsia in base a quale ha meno fila 
	 */
	protected int scegliCorsia() {
		int min = MAX_NUOTATORI;
		int corsia = -1;
		//Ricaviamo la corsia con meno nuotatori
		for(int i = 0 ; i < corsie.length; i++)
			if(corsie[i] < min) {
				min = corsie[i];
				corsia = i;
			}
		
		//Se le corsie sono tutte piene restituiamone una a caso
		if(min == MAX_NUOTATORI) {
			Random r = new Random();
			return r.nextInt(CORSIE);
		}
		//Restituiamo l'indice di corsia più libero
		else return corsia;
	}
	
	/*
	 * Simulazione della piscina
	 * 
	 * parametri numPersone : numero dei nuotatori da far arrivare
	 */
	protected void simula(int numPersone) {
		for(int i = 0; i < numPersone ; i++)
			new Nuotatore(this).start();
		new Istruttore(this).start();
	}
	
}//Piscina
