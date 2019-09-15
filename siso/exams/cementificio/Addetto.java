package siso.exams.cementificio;

class Addetto extends Thread{
	
	private Cementificio c;
	
	
	public Addetto(Cementificio c) {
		this.c = c;
		/*
		 * Essendo questo thread impegnato in un ciclo infinito lo 
		 * settiamo come Daemon in modo da terminarlo quando non ci sono
		 * più clienti
		 */
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				c.iniziaRifornimento();
				rifornisci();
				c.finisciRifornimento();
			}catch(InterruptedException e) {}
		}
	}
	
	private void rifornisci()throws InterruptedException{
		/*
		 * Il tempo di rifornimento è stato abbassato da 5min a  5sec per
		 * velocizzare la simulazione
		 */
		Thread.sleep(5000);
	}

}//Addetto
