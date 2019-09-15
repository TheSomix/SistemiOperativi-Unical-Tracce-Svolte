package siso.exams.boccaccio;

class Addetto extends Thread {

	//Boccaccio della simulazione
	Boccaccio b;
	
	public Addetto(Boccaccio b) {
		this.b = b;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				//Addetto riempie il boccaccio
				b.riempi();
			}catch(InterruptedException e) {}
		}
	}
	
}//Addetto
