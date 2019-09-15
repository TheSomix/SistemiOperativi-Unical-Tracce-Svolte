package siso.exams.piscina;

import java.util.Random;

class Nuotatore extends Thread {
	//Tempi (Da minuti a millisecondi per velocizzare)
	public static final int MIN_N = 300, MAX_N = 600, DOCCIA = 200;
	//Piscina della simulazione
	Piscina p;
	//Generatore Randomico
	Random r = new Random();
	
	public Nuotatore(Piscina p) {
		this.p = p;
	}
	
	@Override
	public void run() {
		int corsia = -1;
		try {
			do {
				//Entriamo nella piscina e scegliamo la corsia
				corsia =p.entra();
				//Se la piscina viene chiusa prima di nuotare usciamo e riproviamo ad entrare alla prossima apertura
			}while(corsia == -1);
			
			nuota();
			//Usciamo dalla corsia per andare a fare la doccia
			p.esci(corsia);
			
		}catch(InterruptedException e) {}
		//Indipendentemente dall'esito andiamo a fare la doccia e usciamo
		finally {
			try {
				if(corsia != -1) {
					doccia();
				}
			} catch (InterruptedException e) {}
		}
	}
	
	private void nuota()throws InterruptedException {
		Thread.sleep(r.nextInt(MAX_N - MIN_N + 1) + MIN_N);
	}
	
	private void doccia()throws InterruptedException{
		Thread.sleep(DOCCIA);
	}

}//Nuotatore
