package siso.exams.cementificio;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CementificioLC extends Cementificio{

	//Coda FIFO All'ingresso
	LinkedList<Thread> fila;
	//Lock per la mutua esclusione
	Lock lock = new ReentrantLock();
	//Condition per l'entrata nel cementificio
	Condition possoEntrare;
	//Condition per il rifornimento dei sacchi da parte dell'addetto
	Condition possoRifornire;
	//Condition per il prelevamento di un sacco 
	Condition possoPrendere;
	
	
	public CementificioLC(int N, int P) {
		super(N, P);
		possoEntrare = lock.newCondition();
		possoRifornire = lock.newCondition();
		possoPrendere = lock.newCondition();
		
		fila = new LinkedList<>();
	}

	@Override
	public void entra() throws InterruptedException {
		lock.lock();
		try {
			//Aggiungiamoci alla fila
			fila.offer(Thread.currentThread());
			//aspettiamo il nostro turno se non c'e' posto nel cementificio
			while(!mioTurno()) 
				possoEntrare.await();
			//togliamoci dalla fila ed entriamo al cementificio
			fila.poll();
			numClienti++;
		}finally {
			lock.unlock();
		}
	}

	/*
	 * restituisce true se si puo' entrare nel cementificio in base alla coda FIFO,
	 * e al numero di clienti massimo
	 */
	private boolean mioTurno() {
		if(numClienti < N && fila.peek() == Thread.currentThread() )
			return true;
		return false;
	}
	
	@Override
	public void esci() throws InterruptedException {
		lock.lock();
		try {
			//Diminuiamo il numero di clienti e segnaliamo il posto libero 
			numClienti--;
			System.out.format("Cliente%d esce dal cementificio %n", Thread.currentThread().getId());
			possoEntrare.signalAll();
		}finally {
			lock.unlock();
		}
	}

	@Override
	public void preleva() throws InterruptedException {
		lock.lock();
		try {
			//Se non ci sono sacchi segnaliamo all'addetto
			if(numSacchi == 0)
				possoRifornire.signal();
			//Aspettiamo di poter ripartire se non ci sono sacchi 
			while(numSacchi == 0 ) {
				possoPrendere.await();
			}
			//Preleviamo un sacco
			numSacchi--;
			System.out.format("Cliente%d preleva un sacco, %d rimanenti %n", Thread.currentThread().getId(), numSacchi);
			
		}finally {
			lock.unlock();
		}
	}

	
	@Override
	public void iniziaRifornimento() throws InterruptedException {
		lock.lock();
		try {
			//Aspettiamo che finiscano i sacchi per rifornire
			while(numSacchi > 0) {
				possoRifornire.await();
			}
			System.out.println("ADDETTO inizia il rifornimento");
			
		}finally {
			lock.unlock();
		}
		
	}

	@Override
	public void finisciRifornimento() throws InterruptedException {
		lock.lock();
		try {
			//Ricarichiamo il numero di sacchi al valore iniziale
			numSacchi = P;
			System.out.println("ADDETTO finisce il rifornimento");
			
			//segnaliamo che i sacchi sono tornati disponibili
			possoPrendere.signalAll();
			
		}finally {
			lock.unlock();
		}
		
	}
	
	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Cementificio c = new CementificioLC(5,100);
		c.simula(200);
	}
		
}//CementificioLC
