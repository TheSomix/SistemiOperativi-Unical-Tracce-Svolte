package siso.exams.salatv;

import java.util.concurrent.Semaphore;

public class SalaSem extends Sala{

	//Semaforo che gestisce l'entrata nella sala 
	Semaphore entrata = new Semaphore(MAX_PERSONE, true);
	//Lista di Semafori che gestisce il numero di persone che aspettano un determinato canale
	Semaphore[] visione = new Semaphore[CANALI];
	//Semaforo che gestisce la mutua esclusione dei thread
	Semaphore mutex = new Semaphore(1);
	
	
	public SalaSem() {
		for(int i = 0; i < CANALI; i++)
			visione[i] = new Semaphore(0);
	}
	
	@Override
	void entra(int c) throws InterruptedException {
		//Attesa all'entrata
		entrata.acquire();
		
		//Entra nella sala
		mutex.acquire();
		richiesta[c]++;
		//Se il canale non e' quello scelto da noi aspettiamo
		if(canaleAttuale != c && canaleAttuale != -1) {
			mutex.release();
			visione[c].acquire();
		}
		else {
			//Impostiamo il canale attuale
			canaleAttuale = c;
			mutex.release();
		}
		System.out.format("Persona%d inizia a guardare il canale %d %n",Thread.currentThread().getId(), c);

	}

	@Override
	void esci(int c) throws InterruptedException {
		mutex.acquire();
		richiesta[c]--;
		System.out.format("Persona%d smette di guardare il canale %d %n",Thread.currentThread().getId(), c);
		//se nessuno vuole guardare piu' il canale scelto da noi cambiamo con quello piu' richiesto
		if(richiesta[c] == 0) {
			canaleAttuale = canaleRichiesto();
			System.out.format("Persona%d cambia canale a  %d %n",Thread.currentThread().getId(), canaleAttuale);
			visione[canaleAttuale].release(richiesta[canaleAttuale]);
		}
		mutex.release();
		//Usciamo dalla sala
		entrata.release();
	}
	
	/*
	 * SIMULAZIONE
	 */
	public static void main(String[] args) {
		Sala s = new SalaSem();
		s.simula(100);
	}

}//SalaSem
