package siso.exams.bar;

import java.util.Random;

class Persona extends Thread {

	//Bar della simulazione
	private Bar bar;
	//Tempi minimi e massimi (in millisecondi)
	public final int MIN_PAG = 500 , MAX_PAG = 1000, MIN_BEVI = 2000, MAX_BEVI = 4000;
	//generatore randomico
	private Random r = new Random();
	//ultima azione 
	int azione;
	
	public Persona(Bar bar) {
		this.bar = bar;
		azione = -1;
	}
	
	@Override
	public void run() {
		try {
			System.out.format("%d entra nel bar %n", this.getId() );
			
			
			//Prima azione
			azione = bar.scegliEInizia();
			bar.inizia(azione);
			operazione(azione);
			bar.finisci(azione);
			
			//Azione successiva 
			azione = (azione + 1) % 2;
			bar.inizia(azione);
			operazione(azione);
			bar.finisci(azione);
		} catch (InterruptedException e) {}
	
		System.out.format("%d esce dal bar %n", this.getId() );
	}	
	
	/*
	 * gestisce i tempi per effettuare le operazioni di pagamento e consumo 
	 * parametri i : id dell'operazione
	 */
	private void operazione(int i)throws InterruptedException{
		/*
		 * I tempi sono stati accorciati per rendere più veloci le simulazioni 
		 */
		switch(i) {
		case Bar.PAGA:
			System.out.format("%d paga alla cassa %n", this.getId() );
			Thread.sleep(r.nextInt(MAX_PAG - MIN_PAG + 1) + MIN_PAG);
			break;
		case Bar.BEVI:
			System.out.format("%d beve al bancone %n", this.getId() );
			Thread.sleep(r.nextInt(MAX_BEVI - MIN_BEVI + 1) + MIN_BEVI);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}//operazione
	
}//class
