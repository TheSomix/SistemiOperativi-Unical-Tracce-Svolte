package siso.exams.piscina;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PiscinaLC extends Piscina{

	//Lock per la mutua esclusione
	Lock lock = new ReentrantLock();
	//Coda delle corsie
	LinkedList<Thread>[] filaCorsie;
	//Condition di apertura della piscina
	Condition aperta;
	//Condition posto libero nella corsia
	Condition[] possoNuotare;
	
	@SuppressWarnings("unchecked")
	public PiscinaLC() {
		super();
		filaCorsie = (LinkedList<Thread>[]) new LinkedList[CORSIE];
		aperta = lock.newCondition();
		possoNuotare = new Condition[CORSIE];
		//Inizializiamo l'array possoNuotare e filaCorsie
		for (int i = 0; i < CORSIE; i++) {
			filaCorsie[i] = new LinkedList<Thread>();
			possoNuotare[i] = lock.newCondition();
		}
	}

	@Override
	public int entra() throws InterruptedException {
		lock.lock();
		try {
			//aspetta se la piscina e' chiusa
			while(!piscinaAperta) {
				aperta.await();
			}
			//Sceglie la corsia
			int corsia = scegliCorsia();
			//Aspetta il suo turno d'ingresso
			filaCorsie[corsia].offer(Thread.currentThread());
			while(!mioTurno(corsia))
				possoNuotare[corsia].await();
			//Entra in piscina se è ancora aperta altrimenti esce subito
			if(!piscinaAperta) {
				return -1;
			}
			filaCorsie[corsia].poll();
			//Si aggiunge alla corsia e inizia a nuotare
			corsie[corsia]++;
			nuotatori.add(Thread.currentThread());
			System.out.format("Nuotatore%d inizia a nuotare nella corsia %d %n", Thread.currentThread().getId(),corsia);
			return corsia;
		}finally {
			lock.unlock();
		}
	}
	
	private boolean mioTurno(int c) {
		if(!piscinaAperta)return true;
		if(corsie[c] < 4 && filaCorsie[c].peek() == Thread.currentThread())return true;
		return false;
	}

	@Override
	public void esci(int corsia) throws InterruptedException {
		lock.lock();
		try {
			//Liberiamo un posto e segnaliamolo ai thread in attesa
			corsie[corsia]--;
			nuotatori.remove(Thread.currentThread());
			possoNuotare[corsia].signalAll();
			System.out.format("Nuotatore%d finisce di nuotare nella corsia %d %n", Thread.currentThread().getId(),corsia);
			
		}finally {
			lock.unlock();
		}
	}

	@Override
	public void apriPiscina() throws InterruptedException {
		lock.lock();
		try {
			//Svuotiamo le code delle corsie
			corsie = new int[CORSIE];
			for(int i = 0; i < CORSIE; i++)
				filaCorsie[i].clear();
			//Segnaliamo l'apertura della Piscina
			piscinaAperta = true;
			System.out.println("Istruttore apre la piscina");
			aperta.signalAll();
			
		}finally {
			lock.unlock();
		}
	}

	@Override
	public void chiudiPiscina() throws InterruptedException {
		lock.lock();
		try {
			//Interrompiamo ogni nuotatore ancora in piscina per mandarlo a fare la doccia
			while(!nuotatori.isEmpty()) {
				Thread t = nuotatori.remove();
				t.interrupt();
			}
			//chiudiamo la piscina
			piscinaAperta = false;
			//Segnaliamo a tutti quelli in attesa per usare la corsia di dover uscire
			for(int i = 0; i < CORSIE; i++)
				possoNuotare[i].signalAll();
			System.out.println("Istruttore Chiude la piscina");
		}finally {
			lock.unlock();
		}
	}
	
	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Piscina p = new PiscinaLC();
		p.simula(500);
	}
}
