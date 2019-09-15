package siso.exams.bar;

public abstract class Bar {

	//Operazioni disponibili sul bar
	public static final int PAGA = 0, BEVI = 1;
	//numero persone della simulazione
	private int n;
	
	public Bar(int n) {
		this.n = n;
	}
	
	/**
	 * permette alla persona di scegliere l'operazione di pagamento o consumazione
	 * restituisce l'id dell'operazione
	 */
	public abstract int scegliEInizia()throws InterruptedException;
	
	/*
	 * permette alla persona di cominciare l'operazione di pagamento o consumazione
	 * parametri i: id dell'operazione
	 */
	public abstract void inizia(int i)throws InterruptedException;
	
	/*
	 * permeette alla persona di finire l'operazione di pagamento o di consumazione 
	 * parametri i: id dell'operazione
	 */
	public abstract void finisci(int i)throws InterruptedException;
	
	public void simula() {
		for(int i = 0; i < n ; i++)
			new Persona(this).start();
	}
	
}//class
