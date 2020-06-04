package siso.exams.funivia;

import java.util.concurrent.Semaphore;

public class FuniviaSem extends Funivia{

	//Semafori per la gestione della salita e della discesa
	Semaphore salitaP,salitaB, discesa;
	//Semafori per segnalare che tutti i turisti siano a bordo o siano scesi
	Semaphore tuttiScesi, tuttiSaliti;
	//Mutua Esclusione
	Semaphore mutex;

	/*
	 *POSSIBILE ALTERNATIVA: Usare un array di semafori per la salita
	 */

	public FuniviaSem() {
		super();
		salitaP = new Semaphore(0);
		salitaB = new Semaphore(0);
		discesa = new Semaphore(0);
		tuttiScesi = new Semaphore(0);
		tuttiSaliti = new Semaphore(0);
		mutex = new Semaphore(1);
	}

	@Override
	public void pilotaStart() throws InterruptedException {
		//Rilasciamo i permessi per la salita dei turisti
		mutex.acquire();
		if(ultimoTipo == PIEDI)
			salitaB.release(3);
		else
			salitaP.release(6);
		mutex.release();
		//Aspettiamo che tutti siano saliti
		tuttiSaliti.acquire();
		System.out.println("Il Pilota fa partire la cabina,si sale");
	}

	@Override
	public void pilotaEnd() throws InterruptedException {
		//Rilasciamo i permessi di discesa
		discesa.release(6);
		mutex.acquire();
		System.out.print("Passeggeri Trasportati: ");
		while(!cabina.isEmpty()) {
			Turista t = cabina.remove();
			System.out.print("Turista"+t.getId() + " di tipo " + t.tipo + "; ");
		}
		System.out.println();
		mutex.release();
		//Aspettiamo che tutti i turisti siano scesi
		tuttiScesi.acquire();
		System.out.println("Il Pilota fa partire la cabina,si scende");
	}

	@Override
	public void turistaSali(int t) throws InterruptedException {
		if(t == PIEDI)
			salitaP.acquire();
		else
			salitaB.acquire();

		mutex.acquire();
		System.out.format("Turista %d di tipo %d sale sulla cabina %n",Thread.currentThread().getId(), t);
		turistiSaliti += t;
		cabina.add((Turista)Thread.currentThread());
		//L'ultimo turista che sale da il permesso al pilota di partire
		if(turistiSaliti == CAPIENZA)tuttiSaliti.release();
		mutex.release();
	}

	@Override
	public void turistaScendi(int t) throws InterruptedException {
		discesa.acquire(t);
		mutex.acquire();
		System.out.format("Turista %d di tipo %d scende dalla cabina %n",Thread.currentThread().getId(), t);
		turistiSaliti -= t;

		//L'ultimo turista che scende da il permesso al pilota di partire e cambia l'ultimo tipo
		if(turistiSaliti == 0) {
			tuttiScesi.release();
			ultimoTipo = t;
		}
		mutex.release();
	}

	public static void main(String[] args) {
		Funivia f = new FuniviaSem();

		f.simula(18,9);
	}


}//FuniviaSem
