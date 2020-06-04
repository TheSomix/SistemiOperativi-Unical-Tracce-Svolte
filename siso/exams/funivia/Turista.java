package siso.exams.funivia;

class Turista extends Thread {

	Funivia f;
	// Piedi -> 1 , Bici -> 2
	int tipo;
	
	public Turista(Funivia f,int tipo) {
		this.f = f;
		this.tipo = tipo;
	}
	
	public void run() {
		try {
			f.turistaSali(tipo);
			f.turistaScendi(tipo);
		}catch(InterruptedException e) {}
	}
}
