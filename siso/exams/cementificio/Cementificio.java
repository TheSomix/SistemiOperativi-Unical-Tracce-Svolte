package siso.exams.cementificio;

public abstract class Cementificio {
	
	//numero clienti accoglibili e sacchi iniziali
	public final int N, P;
	//Numero attuale di clienti
	protected int numClienti = 0;
	//Numero attuale di sacchi
	protected int numSacchi;
	
	public Cementificio(int N, int P) {
		this.N = N;
		this.P = P;
		numSacchi = P;
	}
	
	/*
	 * Usato dal cliente per entrare nel cementificio, gestisce la fila dell'entrata 
	 */
	public abstract void entra()throws InterruptedException;
	
	/*
	 * Usato dal cliente per uscire dal cementificio
	 */
	public abstract void esci()throws InterruptedException;
	
	/*
	 * Usato dal cliente per prelevare un sacco dal cementificio e per segnalare all'addetto se non ce ne sono più
	 */
	public abstract void preleva()throws InterruptedException;
	
	/*
	 * Usato dall'addetto per iniziare a rifornire il cementificio
	 */
	public abstract void iniziaRifornimento()throws InterruptedException;
	
	/*
	 * Usato dall'addetto per segnalare di aver finito di rifornire il cementificio
	 */
	public abstract void finisciRifornimento()throws InterruptedException;
	
	/*
	 * Simulazione
	 */
	protected void simula(int numClienti) {
		new Addetto(this).start();
		
		for(int i = 0; i < numClienti; i++)
			new Cliente(this).start();
	}
	
}//Cementificio