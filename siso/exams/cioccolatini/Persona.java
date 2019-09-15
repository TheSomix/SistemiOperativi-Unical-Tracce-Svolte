package siso.exams.cioccolatini;

import java.util.LinkedList;
import java.util.Random;

class Persona extends Thread {
	//
	public final int MANGIA = 300;
	//Scatola della simulazione
	Scatola s;
	
	
	public Persona(Scatola s) {
		this.s = s;

	}

	@Override
	public void run() {
		try {
			LinkedList<Integer> manciata = s.get();
			System.out.println("Persona "+ this.getId() +"  prende i cioccolatini " + manciata.toString());
			//Mangia se la manciata contiene dei cioccolatini 
			if(!manciata.isEmpty())mangia(manciata);
			System.out.println("Persona" + this.getId() +" restituisce i cioccolatini " + manciata.toString());
			s.put(manciata);
		}catch(InterruptedException e) {}
	}
	
	private void mangia(LinkedList<Integer> c)throws InterruptedException{
		Random r = new Random();
		//Mangiamo un cioccolatino a caso tra quelli passati
		c.remove(r.nextInt(c.size()));
		Thread.sleep(MANGIA);		
	}
	
}//Persona
