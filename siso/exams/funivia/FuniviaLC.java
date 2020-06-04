package siso.exams.funivia;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FuniviaLC extends Funivia{

	//Lock per la mutua esclusione
	Lock lock = new ReentrantLock();
	//Condizioni per gestisce la salita e la discesa dei turisti
	Condition salita, discesa;

	boolean cabinaFerma, arrivo;
	//gestisce la partenza del pilota
	Condition partenza;


	public FuniviaLC() {
		super();
		salita = lock.newCondition();
		discesa = lock.newCondition();
		partenza = lock.newCondition();

	}

	@Override
	public void pilotaStart() throws InterruptedException {
		lock.lock();
		try {
			//Segnala ai turisti di poter salire
			cabinaFerma = true;
			salita.signalAll();
			//Aspettiamo che tutti i turisti siano a bordo
			while(turistiSaliti < CAPIENZA)
				partenza.await();
			System.out.println("Il Pilota fa partire la cabina,si sale");
			cabinaFerma = false;
		}finally {
			lock.unlock();
		}
	}

	@Override
	public void pilotaEnd() throws InterruptedException {
		lock.lock();
		try {
			//Segnala ai turisti di poter scendere
			cabinaFerma = true;
			arrivo = true;
			discesa.signalAll();

			System.out.print("Passeggeri Trasportati: ");

			//Discesa passeggeri
			while(!cabina.isEmpty()) {
				Turista t = cabina.remove();
				System.out.print("Turista"+t.getId() + " di tipo " + t.tipo + "; ");
			}
			System.out.println();

			/*
			 * Aspettiamo che tutti siano scesi,
			 * ATTENZIONE: L'operazione nel while di sopra opera solo sull'array cabina per cui rimuove solo
			 * 				virtualmente i passeggeri, questo passaggio e' ancora necessario
			 */
			while(turistiSaliti > 0) {
				partenza.await();
			}
			cabinaFerma = false;
			arrivo = false;
			System.out.println("Il Pilota fa partire la cabina,si scende");
		}finally {
			lock.unlock();
		}
	}

	@Override
	public void turistaSali(int t) throws InterruptedException {
		lock.lock();
		try {
			while(!mioTurno(t))
				salita.await();
			System.out.format("Turista %d di tipo %d sale sulla cabina %n",Thread.currentThread().getId(), t);
			//non aggiungiamo +1, ma le costanti, cosi' i turisti in bici valgono doppio
			turistiSaliti += t;
			//Aggiungiamo il turista anche all'array cabina
			cabina.add((Turista)Thread.currentThread());
			//Se la cabina e' piena segnaliamo al pilota di poter partire
			if(turistiSaliti == 6)partenza.signal();
		}finally {
			lock.unlock();
		}

	}

	/*
	 * Permette la salita solo se la cabina e' ferma e non piena e se il tipo e' giusto
	 */
	private boolean mioTurno(int tipo) {
		if(ultimoTipo != tipo && cabinaFerma && turistiSaliti < CAPIENZA)
			return true;
		return false;
	}

	@Override
	public void turistaScendi(int t) throws InterruptedException {
		lock.lock();
		try {
			while(!(cabinaFerma && arrivo)) {
				discesa.await();
			}
			System.out.format("Turista %d di tipo %d scende dalla cabina %n",Thread.currentThread().getId(), t);
			//Facciamo scendere il turista
			turistiSaliti -= t;
			//Se e' l'ultimo segnaliamo la partenza
			if(turistiSaliti == 0) {
				ultimoTipo = t;
				partenza.signal();
			}
		}finally {
			lock.unlock();
		}
	}

	/*
	 * Simulazione
	 */
	public static void main(String[] args) {
		Funivia f = new FuniviaLC();
		f.simula(18,9);
	}

}
