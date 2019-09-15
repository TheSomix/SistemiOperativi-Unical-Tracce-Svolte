package siso.exams.bar;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;;



public class BarLC extends Bar{

	//File di bancone e cassa (linkedlist usata come coda)
	LinkedList<Thread> filaCassa, filaBancone;
	//Numero persone al bancone e alla cassa
	int numCassa, numBancone = 0;
	
	//Lock per la mutua esclusione
	Lock lock = new ReentrantLock();
	//Conditions per l'accesso alla cassa o al bancone
	Condition possoPagare, possoBere;
	
	
	public BarLC(int n) {
		super(n);
		filaCassa = new LinkedList<>();
		filaBancone = new LinkedList<>();
		possoPagare = lock.newCondition();
		possoBere = lock.newCondition();
	}
	
	@Override
	public int scegliEInizia() throws InterruptedException {
		int scelta = -1;
		
		lock.lock();
		try {
			//Se la cassa e' vuota il cliente va a pagare
			if(numCassa < 1)scelta = Bar.PAGA;
			//Altrimenti se il bancone ha almeno un posto libero il cliente va a bere
			else if (numBancone < 4)scelta = Bar.BEVI;
			//Se entrambe sono piene il cliente si mette in attesa nella fila piu' corta
			else scelta = filaBancone.size() < filaCassa.size() ? Bar.BEVI : Bar.PAGA;
			
		}finally {
			lock.unlock();
		}
		
		//Restituisce la scelta dell'azione da compiere prima
		return scelta;
	}
	
	@Override
	public void inizia(int i) throws InterruptedException {
		lock.lock();
		try {
			if(i == Bar.BEVI) {
				filaBancone.offer(Thread.currentThread());
				//Ciclo della Condition
				while(!mioTurno(i)) {
					possoBere.await();
				}
				//Incrementiamo persone al bancone
				numBancone++;
				//Liberiamo un posto nella coda
				filaBancone.poll();
			}//if
			else {
				filaCassa.offer(Thread.currentThread());
				//Ciclo della Condition
				while(!mioTurno(i)) {
					possoPagare.await();
				}
				//Incrementiamo persone alla cassa
				numCassa++;
				//Liberiamo un posto nella coda
				filaCassa.poll();
			}//else

		}finally {
			lock.unlock();
		}
	}
	
	/*
	 * metodo per decidere se il thread attuale e' quello da risvegliare a seguito del signalAll in base alla coda FIFO
	 * parametri i : id dell'operazione
	 * restituisce true se e' il turno del thread richiamante, false altrimenti
	 */
	private boolean mioTurno(int i) {
		if(i == Bar.BEVI) {
			if(numBancone < 4 && filaBancone.peek() == Thread.currentThread()) 
				return true;
				
			return false;
		}
		else {
			if(numCassa < 1 && filaCassa.peek() == Thread.currentThread())
				return true;
			return false;
		}
	}
	
	@Override
	public void finisci(int i) throws InterruptedException {
		lock.lock();
		try {
			if(i == Bar.BEVI) {
				//Diminuisco le persone al bancone e rilascio la condition
				numBancone--;
				possoBere.signalAll();
			}
			else {
				//Diminuisco le persone alla cassa e rilascio la condition
				numCassa--;
				possoPagare.signalAll();
			}
		}finally {
			lock.unlock();
		}
	}
	
	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Bar bar = new BarLC(10);
		bar.simula();
	}
	
}//class
