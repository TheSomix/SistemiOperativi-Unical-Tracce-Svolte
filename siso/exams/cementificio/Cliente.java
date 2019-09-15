package siso.exams.cementificio;

import java.util.Random;

class Cliente extends Thread {

	//minimo e massimo numero di sacchi che e' possibile richiedere
	public static final int  MIN_SACCHI =1, MAX_SACCHI = 3;
	//Cementificio della simulazione
	private Cementificio c;
	//Generatore randomico di numeri
	private Random r = new Random();
	//Sacchi da raccogliere
	private int numSacchi;
	
	
	public Cliente(Cementificio c) {
		this.c = c;
		numSacchi = MIN_SACCHI + r.nextInt(1 + MAX_SACCHI - MIN_SACCHI) ;
	}
	
	@Override
	public void run() {
		try {
			//Entriamo nel cementificio
			c.entra();
			//preleviamo uno ad uno i sacchi
			for(int i = 0; i < numSacchi; i++) {
				c.preleva();
				trasporta();
			}
			//Usciamo dal cementificio
			c.esci();
		}catch(InterruptedException e) {}
	}
	
	private void trasporta()throws InterruptedException{
		/*
		 * Il tempo di trasporto e' stato diminuito da 1min ad 1s per
		 * velocizzare la simulazione
		 */
		Thread.sleep(1000);
	}
}//Cliente
