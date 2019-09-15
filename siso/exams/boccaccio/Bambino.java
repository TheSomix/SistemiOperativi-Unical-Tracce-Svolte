package siso.exams.boccaccio;

import java.util.Random;

class Bambino extends Thread {
	//Tempi per mangiare
	final int MIN_M = 400, MAX_M = 800;
	//tipo di caramella preferito
	final int PREF ;
	//Boccaccio della simulazione
	Boccaccio b;
	//Generatore casuale
	Random r = new Random();
	
	
	public Bambino(Boccaccio b) {
		this.b = b;
		PREF = r.nextInt(b.TIPI);
	}
		
	@Override
	public void run() {
		while(true) {
			try {
				//Il bambino prova a prendere la sua caramella preferita
				boolean presa = b.prendi(PREF);
				//Se non ci sono caramelle di quel tipo presa sarà FALSE e il bambino piangera
				if(!presa)b.piangi();
				//Altrimenti il bambino puo' mangiare la caramella presa
				else mangia();
			}catch(InterruptedException e) {}
		}
	}
	
	private void mangia() throws InterruptedException{
		Thread.sleep(MIN_M + r.nextInt(MAX_M - MIN_M + 1));
	}
	
}//Bambino
