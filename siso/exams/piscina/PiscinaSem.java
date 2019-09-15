package siso.exams.piscina;

import java.util.concurrent.Semaphore;

public class PiscinaSem extends Piscina{
	//Contatore di attesa all'entrata della piscina 
	int attesaEntrata = 0;
	//Contatore di attesa all'entrata della corsia
	int[] attesaCorsia = new int[CORSIE];
	//Mutua esclusione sulle variabili condivise
	Semaphore mutex;
	//gestione dell'ingresso alla piscina
	Semaphore ingresso;
	//gestione dell'ingresso alla corsia
	Semaphore[] postoCorsia = new Semaphore[CORSIE];
	
	
	
	public PiscinaSem() {
		super();
		mutex = new Semaphore(1);
		//all'inizio la piscina è chiusa per convenzione
		ingresso = new Semaphore(0);
		//inizializiamo i semafori delle corsie
		for (int i = 0; i < CORSIE; i++) {
			postoCorsia[i] = new Semaphore(MAX_NUOTATORI);
		}
	}

	@Override
	public int entra() throws InterruptedException {
		mutex.acquire();
		//Se la piscina e' chiusa aspettiamone l'apertura
		if(!piscinaAperta) {
			attesaEntrata++;
			mutex.release();
			ingresso.acquire();
			
			mutex.acquire();
			/*Ingresso in piscina*/
			attesaEntrata--;
			mutex.release();
		}
		else mutex.release();
		
		//Scegliamo una corsia e aspettiamo sia libera
		mutex.acquire();
		int c = scegliCorsia();
		attesaCorsia[c]++;
		mutex.release();
		
		//Aspettiamo sul semaforo che ci sia posto
		postoCorsia[c].acquire();
		
		/*Accesso alla corsia garantito*/
		
		mutex.acquire();
		attesaCorsia[c]--;
		//Controlliamo che nell'attesa non sia stata chiusa la piscina
		if(!piscinaAperta) {
			//Restituiamo -1 cosi da sfruttare il loop nella classe Nuotatore
			mutex.release();
			return -1;
		}
		//Se la piscina e' ancora aperta aggiungiamoci alla corsia
		corsie[c]++;
		nuotatori.add(Thread.currentThread());

		System.out.format("Nuotatore%d inizia a nuotare nella corsia %d %n", Thread.currentThread().getId(),c);
		mutex.release();
		//Restituiamo il valore di corsia per sfruttarlo nel metodo "esci"
		return c;
	}

	@Override
	public void esci(int corsia) throws InterruptedException {
		mutex.acquire();
		//Togliamoci dalla corsia
		corsie[corsia]--;
		nuotatori.remove(Thread.currentThread());
		//Rilasciamo un permesso per far entrare qualcun altro al posto nostro
		postoCorsia[corsia].release();
		System.out.format("Nuotatore%d finisce di nuotare nella corsia %d %n", Thread.currentThread().getId(),corsia);
		mutex.release();
	}

	@Override
	public void apriPiscina() throws InterruptedException {
		mutex.acquire();
		//Resettiamo alcuni campi dai valori vecchi  
		corsie = new int[CORSIE];
		//Apriamo la piscina
		piscinaAperta = true;
		//rilasciamo i permessi per entrare nella piscina a chi sta attendendo
		ingresso.release(attesaEntrata);
		System.out.println("Istruttore apre la piscina");
		mutex.release();
	}

	@Override
	public void chiudiPiscina() throws InterruptedException {
		mutex.acquire();
		//Chiudiamo la piscina
		piscinaAperta = false;
		//Rilasciamo i permessi per chi sta aspettando il posto in corsia
		for(int i = 0; i < CORSIE; i++)
			postoCorsia[i].release(attesaCorsia[i] + MAX_NUOTATORI);
		//Interrompiamo ogni nuotatore ancora in piscina per mandarlo a fare la doccia
		while(!nuotatori.isEmpty()) {
			Thread t = nuotatori.remove();
			t.interrupt();
		}
		System.out.println("Istruttore chiude la piscina");
		mutex.release();
	}
	
	
	/*
	 * DEMO
	 */
	public static void main(String[] args) {
		Piscina p = new PiscinaSem();
		p.simula(500);
	}
	
}//PiscinaSem
