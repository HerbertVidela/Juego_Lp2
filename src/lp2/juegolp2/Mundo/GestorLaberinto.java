package lp2.juegolp2.Mundo;

import lp2.juegolp2.Artefactos.*;
import java.util.*;
import java.util.stream.IntStream;

/**
 *
 * @author pmvb
 */
public class GestorLaberinto
{
    List<Laberinto> laberintos;
    
    private final static Arma[] armas = {new Arma(5,10), new Arma(15,25)};
    private final static Armadura[] armaduras = {new Armadura(5), new Armadura(20)};
    private final static PocionCuracion[] posciones = {new PocionCuracion(5),new PocionCuracion(10)};
    
    
    public GestorLaberinto()
    {
        this.laberintos = new ArrayList<Laberinto>();
    }
    
    public Laberinto get(int n)
    {
        return this.laberintos.get(n);
    }
    
    public Laberinto Crear(int M, int N, int[] niveles)
    {
        // pct_enemigo: Minimo 5% - Maximo 20%
        double pct = ((Math.random() * 100) % 11 + 5) / 100;
        Laberinto lab = new Laberinto(2*M+1, 2*N+1, pct, niveles);
        configLaberinto(lab);
        return lab;
    }
    
    public void crearLaberintos(int numLaberintos, int enemy_range)
    {
        for (int i = 0; i < numLaberintos; ++i) {
            // M y N  entre 20 y 30
            int M = (int) ((Math.random()*11) + 20);
            int N = (int) ((Math.random()*11) + 20);
            int[] niveles = IntStream.rangeClosed(i+1, (i+1)*enemy_range).toArray();
            this.laberintos.add(this.Crear(M, N, niveles));
        }
    }
    /**
     * 
     * Agrega lo necesario para completar el laberinto (celdas anterior, siguiente,
     * artefactos y enemigos).
     * Crea lista de celdas libres y luego elige donde poner lo necesario
     * aleatoriamente.
     * 
     * @param lab Laberinto
     */
    private void configLaberinto(Laberinto lab)
    {
        // Crea lista de celdas libres
        ArrayList<Position> libres = new ArrayList<>();
        for (int i = 1; i < lab.getAlto()-1; ++i) {
            for (int j = 1; j < lab.getAncho()-1; ++j) {
                // Si está libre, la añado a la lista
                if (lab.get(i, j).getContenido() == Celda.Contenido.LIBRE) {
                    libres.add(new Position(i, j));
                }
            }
        }
        
        // Agrega anterior
        int index = (int) (Math.random()*libres.size());
        lab.setAnterior(libres.get(index));
        libres.remove(index);
        
        // Agrega siguiente
        index = (int) (Math.random()*100) % libres.size();
        lab.setSiguiente(libres.get(index));
        libres.remove(index);
        
        // Agrega artefactos
        //index = (int) (Math.random()*100) % (libres.size()/2);
        int numero_artefactos = 15; //variable temporal que indica el numero de artefactos por laberinto
        int artefactos_colocados = 0;
        int index_artefacto;
        
        while(artefactos_colocados < numero_artefactos){
            index_artefacto = (int) (Math.random() * 100);
            if(index_artefacto < libres.size()){
                lab.addArtefacto(libres.get(index_artefacto));
                artefactos_colocados++;
            }
        }
        
        ArrayList<Position> usadas = new ArrayList<>();
        // Agrega enemigos
        for (int i = 0; i < libres.size(); ++i) {
            if (Math.random() <= lab.getPctEnemigo()) {
                lab.addEnemigo(libres.get(i));
                usadas.add(libres.get(i));
            }
        }
        // Elimino las posiciones donde ya puse un enemigo
        for (int i = 0; i < usadas.size(); ++i) {
            libres.remove(usadas.get(i));
        }
        //usadas.clear();
        
        /**
         * Por ahora lo haremos así, luego se arregla
         */
        // Crea lista de artefactos a agregar
        //int numArt = ((int) (Math.random() * 6)) + 6;
        Artefacto[] artefactos = new Artefacto[6];
        artefactos[0] = PocionCuracion.pocionesDisp[0];
        artefactos[1] = PocionCuracion.pocionesDisp[1];
        artefactos[2] = Arma.armasDisp[0];
        artefactos[3] = Arma.armasDisp[1];
        artefactos[4] = Armadura.armadurasDisp[0];
        artefactos[5] = Armadura.armadurasDisp[1];
        
        for (int i = 0; i < artefactos.length; ++i) {
            index = (int) (Math.random() * libres.size());
            lab.addArtefacto(libres.get(index), artefactos[i]);
            libres.remove(index);
        }
    }
    
    public void agregaPlayer(Avatar jugador)
    {
        Laberinto laberinto = this.laberintos.get(0);
        laberinto.get(jugador.getPosition()).setContenido(Celda.Contenido.JUGADOR);
    }
    
    public int size()
    {
        return laberintos.size();
    }
}
