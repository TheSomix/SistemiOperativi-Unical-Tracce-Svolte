package siso.exams.cementificio;

import java.util.concurrent.Semaphore;

public class CementificioSem extends Cementificio{
	//Semaforo per la mutua esclusione
	Semaphore mutex;
	//Semaforo per l'ingresso nel cementificio
	Semaphore ingresso;
	//Semaforo per il prelievo di un sacco
	Semaphore sacchi;
	//Semaforo per il rifornimento
	Semaphore sacchiFiniti;
	
	public CementificioSem(int N, int P) {
		super(N, P);
		mutex = new Semaphore(1);
		ingresso = new Semaphore(N, true);
		sacchi = new Semaphore(P);
		sacchiFiniti = new Semaphore(0);
	}

	@Override
	public void entra() throws InterruptedException {
		//Aspettiamo di poter entrare nel cementificio
		ingresso.acquire();
		
		mutex.acquire();
		numClienti++;
		System.out.format("Cliente%d entra nel cementificio %n", Thread.currentThread().getId());
		mutex.release();
	}

	@Override
	public void esci() throws InterruptedException {
		mutex.acquire();
		numClienti--;
		System.out.format("Cliente%d esce dal cementificio %n", Thread.currentThread().getId());
		mutex.release();
		
		//Rilasciamo un posto all'interno del cementificio
		ingresso.release();
	}

	@Override
	public void preleva() throws InterruptedException {
		//Controlliamo che ci siano i sacchi altrimenti segnaliamo
		mutex.acquire();
		if(numSacchi == 0) { 
			sacchiFiniti.release();
			/* 
			 * Escamotage che permette ad un solo cliente di segnalare che i sacchi sono finiti
			 * Attenzione, funziona solo perche' l'addetto imposta la variabile a P e NON la incrementa di P.
			 */
			numSacchi = -1;
		}
		mutex.release();
		//Prendiamo un sacco o mettiamoci in attesa per uno 
		sacchi.acquire();
		mutex.acquire();
		numSacchi--;
		System.out.format("Cliente%d preleva un sacco, %d rimanenti %n", Thread.currentThread().getId(), numSacchi);
		mutex.release();
	}

	@Override
	public void iniziaRifornimento() throws InterruptedException {
		//Aspetta che i sacchi finiscano
		sacchiFiniti.acquire();
		mutex.acquire();
		System.out.println("ADDETTO inizia il rifornimento");
		mutex.release();
	}

	@Override
	public void finisciRifornimento() throws InterruptedException {
		mutex.acquire();
		System.out.println("ADDETTO finisce il rifornimento");
		numSacchi = P;
		mutex.release();
		
		sacchi.release(P);
	}

	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Cementificio c = new CementificioSem(5,10);
		c.simula(200);
	}
	
}//CementificioSEM

