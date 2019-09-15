package siso.exams.boccaccio;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoccaccioLC extends Boccaccio {
	//Lock per la mutua esclusione 
	Lock lock = new ReentrantLock();
	//Condizione per cui possiamo smettere di piangere
	Condition possoPrendere = lock.newCondition();
	//gestisce il rifornimento del boccaccio 
	Condition possoRifornire = lock.newCondition();
	//quando supera 3 l'addetto deve riempire il boccaccio
	int numPianti = 0;
	//flag che segnala che tutte le caramelle sono disponibili
	boolean tutteDisp = false;
	//Generatore casuale di numeri 
	Random r = new Random();
	
	public BoccaccioLC(int TIPI) {
		super(TIPI);
	}

	@Override
	boolean prendi(int c) throws InterruptedException {
		lock.lock();
		try {
			//CASO1: caramelle finite del tipo scelto
			if(boccaccio[c] == 0) {
				tutteDisp = false;
				return false;
			}
			//CASO2: caramella disponibile
			System.out.format("Bambino%d prende caramella %d %n", Thread.currentThread().getId(), c);
			boccaccio[c]--;
			return true;
		}finally {
			lock.unlock();
		}
	}

	@Override
	void piangi() throws InterruptedException {
		lock.lock();
		try {
			numPianti++;
			//Se e' il terzo a piangere segnala all'addetto di riempire il boccaccio
			if(numPianti == 3)possoRifornire.signal();
			System.out.format("Bambino%d inizia a piangere %n", Thread.currentThread().getId());
			while(!tutteDisp) {
				possoPrendere.await();
			}
			System.out.format("Bambino%d smette di piangere %n", Thread.currentThread().getId());
			numPianti--;
			
		}finally {
			lock.unlock();
		}
	}

	@Override
	void riempi() throws InterruptedException {
		lock.lock();
		try {
			while(numPianti < 3 || tutteDisp) {
				possoRifornire.await();
			}
			//Operazione di riempimento del boccaccio
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
			//segnaliamo ai bambini che possono tornare a prendere le caramelle
			tutteDisp = true;
			possoPrendere.signalAll();
			
			System.out.println("ADDETTO HA RIFORNITO: " + Arrays.toString(boccaccio));
		}finally {
			lock.unlock();
		}
		
	}

	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Boccaccio b = new BoccaccioLC(10);
		b.simula(20);
	}
	
}//BoccaccioLC
