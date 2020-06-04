package siso.exams.funivia;

import java.util.LinkedList;

public abstract class Funivia {

	//Costanti, e' stata cambiata la costante tipo per usarla come "peso"  (da "0 e 1" a "1 e 2")
	public static final int PIEDI = 1, BICI = 2,CAPIENZA = 6;
	//contatore dei turisti a bordo della cabina
	int turistiSaliti;
	//Ultimo tipo di turista salito
	int ultimoTipo;
	//array della cabina della funivia
	LinkedList<Turista> cabina;
	
	public Funivia() {
		ultimoTipo = BICI;
		turistiSaliti = 0;
		cabina = new LinkedList<>();
	}
	
	/*
	 * 
	 */
	public abstract void pilotaStart()throws InterruptedException;
	
	/*
	 * 
	 */
	public abstract void pilotaEnd()throws InterruptedException;
	
	/*
	 * 
	 */
	public abstract void turistaSali(int t)throws InterruptedException;
	
	/*
	 * 
	 */
	public abstract void turistaScendi(int t)throws InterruptedException;
	
	/*
	 * Simulazione
	 */
	public void simula(int turAPiedi, int turInBici) {
		//Inizializza turisti a piedi
		for(int i = 0; i < turAPiedi; i++)
			new Turista(this, Funivia.PIEDI).start();
		
		//Inizializza turisti in bici
		for(int i = 0; i < turAPiedi; i++)
			new Turista(this, Funivia.BICI).start();
	
		//inizializza pilota
		new Pilota(this).start();
	}
	
}//Funivia
