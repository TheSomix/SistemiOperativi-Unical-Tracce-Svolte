package siso.exams.piscina;

public class Istruttore extends Thread{

	//TEMPI DI APERTURA E CHIUSURA
	public final int MATTINA = 4000, POMERIGGIO = 5000, PRANZO = 1000, CHIUSURA = 7000;
	//Piscina della simulazione
	Piscina p;
	
	
	public Istruttore(Piscina p) {
		this.p = p;
		setDaemon(true);
	}
	
	
	@Override
	public void run() {
		while(true) {
			try {
				p.apriPiscina();
				apertura(0);
				p.chiudiPiscina();
				chiusura(0);
				p.apriPiscina();
				apertura(1);
				p.chiudiPiscina();
				chiusura(1);
			}catch(InterruptedException e) {}
		}
	}
	
	/*
	 * 
	 */
	private void apertura(int t)throws InterruptedException{
		if(t == 0 )Thread.sleep(MATTINA);
		else Thread.sleep(POMERIGGIO);	
	}
	
	/*
	 * 
	 */
	private void chiusura(int t)throws InterruptedException{
		if(t==0)Thread.sleep(PRANZO);
		else Thread.sleep(CHIUSURA);	
	}
	
	
}//Addetto
