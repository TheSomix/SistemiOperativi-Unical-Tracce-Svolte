package siso.exams.cioccolatini;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ScatolaLC extends Scatola {

	//booleana che determina se la scatola e' impegnata o meno
	boolean scatolaImpegnata;
	//Lock per la mutua esclusione
	Lock lock = new ReentrantLock();
	//Condition per aspettare che nessuno stia impegnando la scatola
	Condition possoMangiare;
	//Generatore randomico di numeri
	Random r = new Random();
	
	public ScatolaLC(int N, int X) {
		super(N, X);
		possoMangiare = lock.newCondition();
	}

	@Override
	LinkedList<Integer> get() throws InterruptedException {
		lock.lock();
		try {
			//Aspettiamo di poter usare la scatola
			while(scatolaImpegnata) {
				possoMangiare.await();
			}
			//Impegnamo la scatola
			scatolaImpegnata = true;
			LinkedList<Integer> manciata = new LinkedList<>();
			//guardiamo al numero di cioccolatini restanti, se la scatola e' vuota usciamo
			int restanti = cioccRestanti();
			if(restanti == 0)return manciata;
			//Riempiamo in modo casuale la manciata
			for(int i = 0 ; i < restanti; i++) {
				//Fermiamoci se arriviamo a 5 
				if(manciata.size() == 5)break;
				//Scegliamo un indice finche' non ne becchiamo uno disponibile
				int scelta = -1;
				do {
					//Scegliamo un tipo di cioccolatino
					scelta = r.nextInt(scatola.length);
				}while(scatola[scelta] == 0);
				//togliamo il cioccolatino dalla scatola 
				scatola[scelta]--;
				//Aggiungiamolo alla lista
				manciata.add(scelta);
			}
			return manciata;
		}finally {
			lock.unlock();
		}
		
	}

	@Override
	void put(LinkedList<Integer> c) throws InterruptedException {
		lock.lock();
		try {
			//Reinseriamo i cioccolatini non scelti 
			for(Integer index : c) {
				scatola[index]++;
			}
			
			//Liberiamo la scatola
			scatolaImpegnata = false;
			possoMangiare.signal();
			
		}finally {
			lock.unlock();
		}
	}

	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Scatola s = new ScatolaLC(100, 5);
		s.simula();
	}
	
}
