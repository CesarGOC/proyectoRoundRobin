  
//package processsManagement;
/**
 * Representa nuestro planificador principal de todo el sistema.
 *
 */

public class Planificador {
	private Queue<Proceso> colaProcesosListos = new LinkedQueue<>();
    private MemoriaRam memoriaRam = new MemoriaRam();
    private Procesador cpu = new Procesador();
    private boolean bajoProcesoCPU = false;
	private static int tiempoTotal;
	private static float resultadoFinalEspera;
	private static float resultadoFinalEjecucion;
	private static float resultadoFinalRespuesta;
    
	public static int tiempo = 0;
    
	public void iniciarSimulacion(Proceso[] tablaProcesos){
        int i=0;
        Proceso proceso = null;
		for(int j=0; j<tablaProcesos.length; j++){
		tiempoTotal += tablaProcesos[j].getTiempoRequeridoEjecucion();
		}
        while(this.tiempo<=this.tiempoTotal) {
            System.out.printf("Tiempo: %d\n", this.tiempo);            
            // Como se que la tabla de procesos ya esta ordena, los posteriores elementos
            // tendran un numero mayor de tiempo de llegada
            if(i < tablaProcesos.length && (this.tiempo == tablaProcesos[i].getTiempoLLegadaProceso() || this.tiempo > tablaProcesos[i].getTiempoLLegadaProceso() )) {
                agregarColaProcesosListo(tablaProcesos[i]);
                i++;
            }
            
            // Siguiendo la politica primero se forman y luego vaja el proceso a la cola, se pone aqui el if.
            if(bajoProcesoCPU) {
                agregarColaProcesosListo(proceso); // proceso el el ultimo valor que guardo.
                bajoProcesoCPU = false; // Regresamos a su valor por defecto.
            }
            
            // Verificamos que exista tamaño en la memoria, que existe tambien proceoso en cola de espera
            // y que tambien exista espacio adecuado para el proceoso.
            if ( memoriaRam.estaDispoble() && !colaProcesosListos.isEmpty() && (memoriaRam.getTamanio() - colaProcesosListos.first().getTamanio()) >= 0 ) {
                proceso = null;
                try{
                    proceso = colaProcesosListos.dequeue();
                } catch (EmptyCollectionException e) {}
                System.out.printf("Subio el proceso %s a la memoria\n", proceso.getNombre());
                System.out.println("***Insertando en la memoria RAM***");
                dormir();
                //System.out.println("Ram tamanio " + memoriaRam.getTamanio());
                //System.out.println("Proceso tamanio " + proceso.getTamanio());
                int tamanioRestante = memoriaRam.getTamanio() - proceso.getTamanio();                
                memoriaRam.insertarProceso(proceso);
                System.out.println(memoriaRam);
                memoriaRam.setTamanio(tamanioRestante);
                System.out.printf("Espacio restante RAM: %dKB\n", memoriaRam.getTamanio());
                proceso = null;
            }

            if (cpu.estaDispoble() && memoriaRam.tieneProcesosCargados()) {
                proceso = null;
                proceso = memoriaRam.sacarProceso();
                System.out.printf("Subio el proceso %s a la CPU\n", proceso.getNombre());
                System.out.println("---Insertando a la CPU---");
                cpu.ejecutar(proceso);
                if(proceso.getTiempoRequeridoEjecucion() > 0) {
                    bajoProcesoCPU = true;
                } else {
                    proceso = null;
                }
                dormir();
            }            
            
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e){}
            //cpu.tiempoEjecucionP=this.tiempo;
			this.tiempo += cpu.quantum;
			cpu.tiempoEjecucionT = this.tiempo;
        }
		
		for(int j=0; j<tablaProcesos.length; j++){
		System.out.println("-----------------------------------------------------------------------------------------------");
		System.out.println("Proceso: "+tablaProcesos[j].getNombre()+" tiempo ejecucion antes = "+tablaProcesos[j].tiempoQuantumAntes+" msg");
		System.out.println("Proceso: "+tablaProcesos[j].getNombre()+" tiempo ejecucion suma = "+tablaProcesos[j].tiempoQuantumSuma+" msg");
		System.out.println("Proceso: "+tablaProcesos[j].getNombre()+" tiempo ejecucion total = "+tablaProcesos[j].tiempoQuantumTotal+" msg");
		System.out.println("Proceso: "+tablaProcesos[j].getNombre()+" primer tiempo de ejecucion = "+tablaProcesos[j].getTiempoLLegadaProceso()+" msg");
		}
		System.out.println("-----------------------------------------------------------------------------------------------");
		for(int x=0; x<tablaProcesos.length; x++){
		resultadoFinalEspera+= (float) tiempoDeEsperaTotal(tablaProcesos[x].tiempoQuantumAntes,tablaProcesos[x].getTiempoLLegadaProceso(),tablaProcesos[x].tiempoQuantumSuma);
		}
		System.out.println("Tiempo de espera total es = "+resultadoFinalEspera/tablaProcesos.length+" msg");
		
		for(int x=0; x<tablaProcesos.length; x++){
		resultadoFinalEjecucion+= (float) tiempoDeEjecucionTotal(tablaProcesos[x].tiempoQuantumTotal,tablaProcesos[x].getTiempoLLegadaProceso());
		}
		System.out.println("Tiempo de ejecucion total es = "+resultadoFinalEjecucion/tablaProcesos.length+" msg");
		
		for(int x=0; x<tablaProcesos.length; x++){
		resultadoFinalRespuesta+= (float) tiempoDeRespuestaTotal(tablaProcesos[x].getTiempoLLegadaProceso(),tablaProcesos[x].getTiempoLLegadaProceso());
		}
		System.out.println("Tiempo de espera total es = "+resultadoFinalRespuesta/tablaProcesos.length+" msg");
        
	}


	/**
	 * Agregar a la cola de procesos listos para despues ingresar a la memoria.
     * 
	 */
	public void agregarColaProcesosListo(Proceso proceso) {
        System.out.println("Insertando a la cola procesos listos ...");
        dormir();
        colaProcesosListos.enqueue(proceso);
        System.out.println(colaProcesosListos);

	}

    private void dormir() {
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e){}
    }
	
	public int tiempoDeEsperaTotal(int tiempoEjecucionAntes, int tiempoLlegada, int tiempoSumaAntes ){
		int valorEsp;
		valorEsp = tiempoEjecucionAntes-tiempoLlegada-tiempoSumaAntes;
		return valorEsp;
	}
	public int tiempoDeRespuestaTotal(int primeraEjecucion, int tiempoLlegada){
		int valorRes;
		valorRes = primeraEjecucion-tiempoLlegada;
		return valorRes;
	}
	
	public int tiempoDeEjecucionTotal(int tiempoEjecucionTotal, int tiempoLlegada){
		int valorEje; 
		valorEje = tiempoEjecucionTotal-tiempoLlegada;
			return valorEje;
	}

    
}