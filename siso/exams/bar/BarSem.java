package siso.exams.bar;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class BarSem extends Bar{

	//Semafori di cassa e Bancone
	private HashMap<Integer,Semaphore> operazioni;

	//Semaforo mutua esclusione
	private Semaphore mutex = new Semaphore(1);
	
	//File di cassa e bancone
	private int filaBancone = -4;
	private int filaCassa = -1;
	
	
	public BarSem(int n) {
		super(n);
		operazioni = new HashMap<>();
		//Semaforo della cassa
		operazioni.put(Bar.PAGA, new Semaphore(1, true));
		//Semaforo del bancone
		operazioni.put(Bar.BEVI, new Semaphore(4, true));
	}

	@Override
	public int scegliEInizia() throws InterruptedException {
		int scelta;
		mutex.acquire();
		/*Sezione Critica*/

		/*
		 * Se una fila e' negativa vuol dire che c'e' posto, una fila positiva e' una coda effettiva
		 * per cui se c'e' posto alla cassa il cliente si dirige direttamente li 
		 */
		if(filaCassa < 0 )scelta = Bar.PAGA;
		else if(filaBancone < 0)scelta = Bar.BEVI;
		//Altrimenti il cliente si inserisce nella coda minore
		else scelta = filaBancone < filaCassa ? Bar.BEVI : Bar.PAGA;
				
		mutex.release(); 
		/*FINE sezione critica*/
		
		return scelta;
	}

	@Override
	public void inizia(int i) throws InterruptedException {
		//Semaforo scelto
		Semaphore operazione = operazioni.get(i);
		
		mutex.acquire();
		/*Sezione Critica*/
		
		//Aggiungiamoci all'eventuale fila, o in caso diminuiamo i posti liberi
		if(i == Bar.PAGA)filaCassa++;
		else filaBancone++;
		
		/*FINE sezione critica*/
		mutex.release();
		
		//acquisiamo il semaforo
		operazione.acquire();
	}

	@Override
	public void finisci(int i) throws InterruptedException {
		//Scegliamo il semaforo in base all'operazione
		Semaphore operazione = operazioni.get(i);
		
		mutex.acquire();
		/*Sezione Critica*/
		
		//Liberiamo un posto nella fine
		if(i == Bar.PAGA)filaCassa--;
		else filaBancone--;
		//Aggiungiamo un permesso all'operazione che abbiamo finito 
		operazione.release();
		
		/*FINE sezione critica*/
		mutex.release();
	}

	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Bar bar = new BarSem(10);
		bar.simula();
	}

}//Class
