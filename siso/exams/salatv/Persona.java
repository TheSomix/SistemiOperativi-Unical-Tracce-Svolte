package siso.exams.salatv;

import java.util.Random;

class Persona extends Thread {
	//Minimo e massimo tempo che vogliamo stare a guardare il canale 
	final int MIN_T = 300, MAX_T = 3000;
	//Sala della simulazione
	Sala s;
	//Generatore randomico
	Random r = new Random();
	
	public Persona(Sala s) {
		this.s = s;
	}
	
	@Override
	public void run() {
		//Scegliamo il canale
		int c = r.nextInt(s.CANALI);
		try {
			s.entra(c);
			guarda();
			s.esci(c);			
		}catch(InterruptedException e) {}
	}
	
	private void guarda()throws InterruptedException{
		Thread.sleep(MIN_T + r.nextInt(MAX_T - MIN_T + 1));
	}
	
}//Persona
