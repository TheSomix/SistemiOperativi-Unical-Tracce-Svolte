package siso.exams.cioccolatini;

import java.util.LinkedList;

public abstract class Scatola {
	//Numero di Cioccolatini e di Tipi diversi
	public final int N, X;
	/* array che ha come indice il numero di cioccolatini e
	 * come valore quanti cioccolatini ci sono di quel tipo  
	 */
	public int[] scatola;
	
	
	public Scatola(int N , int X) {
		if(N % X != 0)throw new IllegalArgumentException("JAVA IS FANTASTIC");
		this.N = N;
		this.X = X;
		
		scatola = new int[X];
		for(int i = 0; i < X; i++)
			scatola[i] = N/X;
	}
	
	/*
	 *	Permette ad una persona di prendere una manciata di cioccolatini e restituisce una lista di interi dove 
	 *	ogni elemento contiene il tipo di cioccolatino prelevato
	 */
	abstract LinkedList<Integer> get() throws InterruptedException;
	
	/*
	 * 	Permette ad una persona di rimettere nella scatola i cioccolatini non scelti.
	 * 	Rivece una lista di interi dove ogni elemento contiene il tipo di cioccolatino e
	 * 	Restituisce una lista che contiene il tipo di cioccolatini da restituire 
	 */
	abstract void put(LinkedList<Integer> c) throws InterruptedException;
	
	/*
	 *	Tiene il conto dei cioccolatini totali restanti  
	 */
	protected int cioccRestanti() {
		int sum = 0;
		for(int i = 0; i < X; i++) 
			sum += scatola[i];
		return sum;
	}
	
	
	void simula() {
		for(int i = 0; i < N; i++)
			new Persona(this).start();
	}
	
}//Scatola
