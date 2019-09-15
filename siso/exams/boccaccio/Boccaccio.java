package siso.exams.boccaccio;

public abstract class Boccaccio {
	//Numero caramelle massime nel boccaccio
	final int MAX_CARAMELLE = 100;
	//Numero di tipi di caramelle diverse
	final int TIPI ;
	//array di caramelle che rappresenta il boccaccio
	int[] boccaccio;
	
	
	public Boccaccio(int TIPI) {
		this.TIPI = TIPI;
		boccaccio = new int[TIPI];
	}
	
	/*
	 *	Permette al bambino di prendere la caramella del tipo C
	 *	Restituisce false se le caramelle del tipo c sono terminate, true altrimenti
	 */
	abstract boolean prendi(int c) throws InterruptedException;
	
	/*
	 *	Se le caramelle del tipo c sono finite il bambino si mette a piangere 
	 */
	abstract void piangi()throws InterruptedException;
	
	/*
	 *	Permette all'addetto di riempire il boccaccio una volta che ci sono almeno tre 
	 *	bambini che piangono 
	 */
	abstract void riempi()throws InterruptedException;
	
	/*
	 *	Restituisce il numero di caramelle totali attuali
	 */
	int caramelle() {
		int sum = 0;
		for (int i = 0; i < TIPI; i++) {
			sum += boccaccio[i]; 
		}
		return sum;
	}
	
	/*
	 * Simulazione con "numBambini" bambini
	 */
	void simula(int numBambini) {
		for(int i = 0; i < numBambini; i++) {
			new Bambino(this).start();
		}
		new Addetto(this).start();
	}
	
}//BOCCACCIO
