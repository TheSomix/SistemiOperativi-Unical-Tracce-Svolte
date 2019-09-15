package siso.exams.boccaccio;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class BoccaccioSem extends Boccaccio{

	/*
	 * Mutex : semaforo per la mutua esclusione da utilizzare per accedere a variabili condivise (e.g. Contatori)
	 * Riempi: semaforo per gestire le richieste di riempimento del boccaccio
	 * Caramelle: semaforo per gestire l'avvenuto rifornimento 
	 */
	Semaphore mutex, riempi, caramelle;
	
	//Generatore casuale
	Random r = new Random();
	
	//Numero di bambini che piangono 
	int numPianti = 0;
	
	public BoccaccioSem(int TIPI) {
		super(TIPI);
		mutex = new Semaphore(1);
		caramelle = new Semaphore(0);
		riempi = new Semaphore(0);
	}

	@Override
	boolean prendi(int c) throws InterruptedException {
		mutex.acquire();
		if(boccaccio[c] == 0) {
			mutex.release();
			return false;
		}
		System.out.format("Bambino%d prende caramella %d %n", Thread.currentThread().getId(), c);
		boccaccio[c]--;
		mutex.release();
		return true;
	}

	@Override
	void piangi() throws InterruptedException {
		mutex.acquire();
		System.out.format("Bambino%d inizia a piangere %n", Thread.currentThread().getId());
		numPianti++;
		if(numPianti <= 3)
			riempi.release();
		mutex.release();
		
		//Aspettiamo ci siano abbastanza caramelle
		caramelle.acquire();
		
		mutex.acquire();
		System.out.format("Bambino%d smette di piangere %n", Thread.currentThread().getId());
		numPianti--;
		mutex.release();
		
	}

	@Override
	void riempi() throws InterruptedException {
		riempi.acquire(3);
		
		//Riempiamo il boccaccio
		mutex.acquire();
		System.out.println("ADDETTO INIZIA A RIFORNIRE");
		//Somma parziale delle caramelle
		int parSum = 0;
		//Portiamo il numero minimo di ogni tipo a 3
		for(int i = 0; i < TIPI; i++) {
			if(boccaccio[i] < 3) {
				boccaccio[i] += 3 - boccaccio[i];
			}
			parSum+= boccaccio[i];
		}
		//Riempiamo casualmente il boccaccio
		for(int i = 0; i < TIPI && parSum < MAX_CARAMELLE; i++) {
			int aggiunta = r.nextInt(MAX_CARAMELLE - parSum);
			boccaccio[i] += aggiunta;
			parSum += aggiunta;
		}
		//Gestiamo un evenutale residuo
		if(parSum < MAX_CARAMELLE) {
			boccaccio[r.nextInt(TIPI)] += MAX_CARAMELLE - parSum;
		}
		System.out.println("ADDETTO HA RIFORNITO: " + Arrays.toString(boccaccio));
		
		//Segnaliamo ai bambini in attesa l'avvenuto riempimento
		caramelle.release(numPianti);
		mutex.release();
	}

	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Boccaccio b = new BoccaccioSem(10);
		b.simula(20);
	}
	
}//BoccaccioSem 
