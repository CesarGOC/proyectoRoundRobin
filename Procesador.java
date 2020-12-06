public class Procesador {

    private boolean disponible;
    int quantum;
	int tiempoEjecucionT;
	int tiempoEjecucionP;
	int contadorFaltante;
    public Procesador() {
        disponible = true;
        quantum = 4;
		tiempoEjecucionT=0;
		tiempoEjecucionP=0;
		contadorFaltante=0;
    }

    public boolean estaDispoble() {
        return disponible == true;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public void ejecutar(Proceso proceso) {
		this.quantum = 4;
        for(int i = 0; i < this.quantum; i++) {
            if(proceso.getTiempoRequeridoEjecucion() == 0){
				this.quantum = i;
				proceso.tiempoQuantumAntes = tiempoEjecucionT;
				proceso.tiempoQuantumTotal = tiempoEjecucionT+i;
				return;
			}
            System.out.printf("%s en ejecucion %d msg\n", proceso.getNombre(), proceso.getTiempoRequeridoEjecucion());
            dormirProcesador(1000);
			int aux = proceso.getTiempoRequeridoEjecucion();
            proceso.setTiempoRequeridoEjecucion(--aux);
        }
		this.quantum =4;
		//proceso.tiempoQuantumF=tiempoEjecucionP;
		
		//Codigo para obtener el tiempo de ejecucion antes
		if (proceso.getTiempoRequeridoEjecucion() == 0){
			proceso.tiempoQuantumSuma += contadorFaltante;
		}else{
			proceso.tiempoQuantumSuma += this.quantum;
		}
		//Codigo para obtener el tiempo de ejecucion anterior
		proceso.tiempoQuantumAntes = tiempoEjecucionT;
		//Codigo para obetenr el tiempo de ejecucion total
		proceso.tiempoQuantumTotal = tiempoEjecucionT+this.quantum;
	}

    private void dormirProcesador(int tempo) {
        try{
            Thread.sleep(tempo);
        } catch (InterruptedException e){}
    }
   
}