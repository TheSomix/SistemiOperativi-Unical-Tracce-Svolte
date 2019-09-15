package siso.exams.salatv;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SalaLC extends Sala{
	
	//Lock per la mutua esclusione
	Lock lock = new ReentrantLock();
	
	//Array di condition che gestisce le persone che aspettano un determinato canale 
	Condition[] visione = new Condition[CANALI];
	//Condizione di entrata nella sala
	Condition entrata;
	
	public SalaLC() {
		for(int i = 0; i < CANALI; i++)
			visione[i] = lock.newCondition();
		
		entrata = lock.newCondition();
	}
	
	@Override
	void entra(int c) throws InterruptedException {
		lock.lock();
		try {
			//Aspettiamo di poter entrare nella sala
			while(numPersone > MAX_PERSONE)
				entrata.await();
			//Entrati nella sala
			numPersone++;
			richiesta[c]++;
			//CASO 1: nessuno stava guardando la tv, iniziamo a guardarla noi	
			if(numPersone == 1) {
				canaleAttuale = c;
				visione[c].signalAll();
			}
			//CASO 2: Qualcuno sta gia'  guardando la tv
			else {
				//se il canale e' diverso aspettiamo di poter iniziare a guardare la tv
				while(canaleAttuale != c)
					visione[c].await();
			}
			System.out.format("Persona%d inizia a guardare il canale %d %n",Thread.currentThread().getId(), c);
		}finally {
			lock.unlock();
		}
	}

	@Override
	void esci(int c) throws InterruptedException {
		lock.lock();
		try {
			System.out.format("Persona%d smette di guardare il canale %d %n",Thread.currentThread().getId(), c);
			richiesta[c]--;
			//Se nessuno guarda piu' il canale impostato cambiamolo con il piu' richiesto
			if(richiesta[c] == 0) {
				canaleAttuale = canaleRichiesto();
				System.out.format("Persona%d cambia canale a  %d %n",Thread.currentThread().getId(), canaleAttuale);
				visione[canaleAttuale].signalAll();
			}
			//Usciamo dalla stanza
			numPersone--;
			entrata.signal();
		}finally {
			lock.unlock();
		}
	}

	/*
	 * SIMULAZIONE
	 */
	public static void main(String[] args) {
		Sala s = new SalaLC();
		s.simula(100);
	}
	
}//SalaLC
