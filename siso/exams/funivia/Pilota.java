package siso.exams.funivia;

class Pilota extends Thread {

	//Funivia di riferimento
	Funivia f;

	public Pilota(Funivia f) {
		this.f = f;
		//Pilota impostato come Daemon per far terminare il programma quando non ci sono piu' passeggeri
		this.setDaemon(true);
	}

	public void run() {
		while(true) {
			try {
				f.pilotaStart();
				salita();
				f.pilotaEnd();
				discesa();
			}catch(InterruptedException e) {}
		}
	}

	private void salita()throws InterruptedException{
		Thread.sleep(500);
	}
	private void discesa()throws InterruptedException{
		Thread.sleep(200);
	}

}//Pilota
