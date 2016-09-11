package lp2.juegolp2;

import java.util.*;

/**
 * Singleton, no debería haber más de una instancia de Juego
 * 
 * @author pmvb
 */
public class Juego {
    private static final Juego INSTANCE = new Juego();
    private static final String[] availableCommands = {
        "help",
        "interactuar",
        "mirar",
        "mover",
        "salir"
    };
    
    private Avatar jugador;
    private GestorLaberinto gestorLaberinto;
    private Dibujador dibujador;
    private int currentLabIndex;
    private int numLaberintos;
    
    private Juego()
    {
        this.gestorLaberinto = new GestorLaberinto();
        this.dibujador = new Dibujador();
        this.currentLabIndex = 0;
    }
    
    public static Juego getInstance()
    {
        return Juego.INSTANCE;
    }
    
    // Introducción al juego
    public void intro()
    {
        System.out.println("JUEGO");
        this.historia();
    }
      
    private void historia(){
        String nJugador = this.jugador.getNombre();        
        System.out.println("A través del tiempo y el espacio se abren puertas.");
        System.out.println("Mundo paralelos se crean todos los días con acciones pequeñas.");
        System.out.println("Hay mundos maravillosos con historias y leyendas nunca antes contadas");
        System.out.println("Sin embargo...");
        System.out.println("No todos los mundos son amigables.");
        System.out.print("Un día normal de su vida, ");
        System.out.print(nJugador);
        System.out.println(" es transportado hacia el fantástico mundo de Aether.");
        System.out.println("Aether está dominado por el demonio Azazel");
        System.out.println("Azazel planea unir los mundos y convertirse en el amo supremo");
        System.out.print(nJugador);
        System.out.println(" lo detendrá, no porque lo desee, sino porque es el único que puede hacerlo.");
        System.out.printf("Avanza, %s\n",nJugador);
        this.pauseScreen();
    }
    
    // Configura lo necesario para jugar
    public void init()
    {
        this.initMap();
        // Obten datos y crea jugador
        this.initPlayer();
        
    }

    public Result play()
    {
        Scanner scan = new Scanner(System.in);
        Laberinto laberintoActual = this.gestorLaberinto.get(this.currentLabIndex);
        while(true){
            this.dibujador.dibujarLaberinto(laberintoActual, this.jugador.getPosition());
            this.dibujador.dibujarInfoJugador(this.jugador);
            System.out.print("Ingrese su siguiente movimiento (Ingrese help para ver los comandos disponibles) : ");
            
            String[] cmd = this.getCommandFromString(scan.nextLine());
            if (!this.verifyCommand(cmd)) {
                System.err.println("No se ha ingresado un comando válido");
                this.showHelp();
            } else {
                switch (cmd[0]) {
                    case "help":
                        this.showHelp();
                        break;
                    case "interactuar":
                        this.interactuarMundo(laberintoActual);                        
                        break;
                    case "mover":
                        this.moverAvatar(cmd[1], laberintoActual);
//                        this.moverEnemigos(laberintoActual);
                        if(this.jugador.getPosition().equals(laberintoActual.getSiguiente())){
                            if(++this.currentLabIndex == this.gestorLaberinto.size()) {
                                return Result.WIN;
                            } else {
                                laberintoActual = this.gestorLaberinto.get(this.currentLabIndex);
                                this.jugador.setPosition(laberintoActual.getAnterior());
                            }
                        }
                        else if(this.jugador.getPosition().equals(laberintoActual.getAnterior())){
                            if(this.currentLabIndex >= 1){
                                this.currentLabIndex--;
                                laberintoActual = this.gestorLaberinto.get(this.currentLabIndex);
                                this.jugador.setPosition(laberintoActual.getSiguiente());
                            }
                        }
                        break;
                    case "mirar":
                        this.playerFaceDirection(cmd[1]);
                        break;
                    case "salir":
                        return Result.QUIT;
                }
            }
        }
    }
    
    private String[] getCommandFromString(String line)
    {
        return line.split(" ");
    }
    
    private boolean verifyCommand(String[] cmd)
    {
        // Si no ha ingresado ningun comando
        if (cmd.length <= 0)
            return false;
        // Limpia la entrada
        cmd[0] = cmd[0].toLowerCase();
        // Si el comando no está disponible
        if (!Arrays.asList(availableCommands).contains(cmd[0].toLowerCase()))
            return false;
        // Si trata de moverse, pero la direccion no es valida
        if (cmd[0].equals("mover") || cmd[0].equals("mirar")) {
            if (cmd.length < 2)
                return false;
            cmd[1] = cmd[1].toUpperCase();
            if (!Direction.contains(cmd[1]))
                return false;
        }
        return true;
    }
    
    public void showHelp()
    {
        System.out.println("Comandos disponibles: ");
        /**
         * Ayuda
         */
        System.out.println("help:\t\tMuestra este mensaje de ayuda");
        /**
         * Mover
         */
        System.out.println("mover <dir>:\tMueve al jugador en la direccion dir");
        System.out.println("\t\tDonde dir puede ser:");
        System.out.println("\t\t'UP': arriba");
        System.out.println("\t\t'DOWN': abajo");
        System.out.println("\t\t'RIGHT': derecha");
        System.out.println("\t\t'LEFT': izquierda");
        /**
         * Mirar
         */
        System.out.println("mirar <dir>:\tMira en la direccion dir");
        System.out.println("\t\tDonde dir puede tener los mismos valores que al mover el jugador");
        /**
         * Interactuar
         */
        System.out.println("interactuar:\tInteractua con un artefacto en la casilla adyacente en la direccion que estás mirando");
        /**
         * Salir
         */
        System.out.println("salir:\t\tTermina el juego inmediatamente.");
        /**
         * Pause the screen
         */
        this.pauseScreen();
    }
    
    public Result result()
    {
        // Verifica si el jugador ha perdido o gano, o si sigue jugando
        /**
         * Si el jugador está en la posición siguiente del último laberinto, 
         * ha ganado
         */
        if (this.currentLabIndex == this.gestorLaberinto.size()-1
            &&
            this.jugador.getPosition().equals(this.gestorLaberinto.get(numLaberintos).getSiguiente())) {
            return Result.WIN; 
        }
        if (this.jugador.getCurrentHP() == 0) {
            return Result.LOSE;
        }
        return Result.WIN;
    }
    
    private void initPlayer()
    {
        Scanner scan = new Scanner(System.in);
        System.out.print("Ingrese su nombre: ");
        String nombre = scan.nextLine();
        Laberinto currentLab = this.gestorLaberinto.get(this.currentLabIndex);
        Position avatarPos = new Position(currentLab.getAnterior());
        Arma armaInicial = new Arma(1, 5);
        this.jugador = new Avatar(nombre, avatarPos);
        this.jugador.setArma(armaInicial);
        this.gestorLaberinto.agregaPlayer(jugador);
    }

    private void initMap()
    {
        this.numLaberintos = (int) (Math.random() * 6) + 5;
        this.gestorLaberinto.crearLaberintos(numLaberintos);
    }
    
    private void moverAvatar(String mov, Laberinto laberintoActual) 
    {
        Direction dir = Direction.valueOf(mov);
        // Si no se puede mover a la posición seleccionada, se envía un mensaje
        if (!laberintoActual.validPlayerPosition(this.jugador.getPosition().copy().move(dir))) {
            this.dibujador.showError("No se puede mover a esa posición");
            pauseScreen();
            return;
        }
        Position playerPos = this.jugador.getPosition();
        Celda currCell = laberintoActual.get(playerPos);
        if (playerPos.equals(laberintoActual.getAnterior())) {
            // Si está sobre la celda ANTERIOR antes de moverse, lo pinta de nuevo
            currCell.setContenido(Celda.Contenido.ANTERIOR.asChar());
        } else if (playerPos.equals(laberintoActual.getSiguiente())) {
            // Si está sobre la celda SIGUIENTE antes de moverse, lo pinta de nuevo
            currCell.setContenido(Celda.Contenido.SIGUIENTE.asChar());
        } else {
            currCell.setContenido(Celda.Contenido.LIBRE.asChar());
        }
        this.jugador.move(dir);
    }
    
    private void moverEnemigos(Laberinto laberintoActual){
        int rand = (int)(Math.random()*4);
        Direction directions[] = Direction.values();
        Direction dir = directions[rand];
        for(int i = 1; i<laberintoActual.getAlto()-1;i++)
            for(int j = 1; j<laberintoActual.getAncho(); j++){
                if(laberintoActual.get(i,j).getEnemigo() != null){
                    if(laberintoActual.validPlayerPosition(laberintoActual.get(i,j).getEnemigo().getPosition().copy().move(dir))){
                        Enemigo enemigo = laberintoActual.get(i,j).getEnemigo();
                        // Saco el enemigo de la posicion anterior
                        laberintoActual.get(i,j).setEnemigo(null);
                        laberintoActual.get(i,j).setContenido(Celda.Contenido.LIBRE);
                        enemigo.move(dir);
                        // Agrego al enemigo a la nueva posicion
                        laberintoActual.get(enemigo.getPosition()).setEnemigo(enemigo);
                        laberintoActual.get(enemigo.getPosition()).setContenido(Celda.Contenido.ENEMIGO);
                    }
                }
            }
    }
    
    private void interactuar(Laberinto laberintoActual, Position pos){
        if(laberintoActual.get(pos).getContenido() != Celda.Contenido.PARED.asChar()){
            System.out.println("Entra a la condicion");
            Artefacto artefacto = laberintoActual.get(pos).getArtefacto();
            Enemigo enemigo = laberintoActual.get(pos).getEnemigo();
            if(artefacto != null){
                this.jugador.pickupItem(artefacto);
                laberintoActual.get(pos).setContenido(Celda.Contenido.LIBRE);
                laberintoActual.get(pos).setArtefacto(null);
            }
            else if(enemigo != null){
                int damage = this.jugador.getArma().damage();
                enemigo.damage(damage);
                System.out.println("El enemigo recibio " + damage + "de danho");
                System.out.println("Vida actual del enemigo: " + enemigo.getCurrentHP());
                if(enemigo.getCurrentHP() == 0) {
                    laberintoActual.get(pos).setContenido(Celda.Contenido.LIBRE);
                    laberintoActual.get(pos).setEnemigo(null);
                }
                this.pauseScreen();
            }
        }        
    }
    
    private void interactuarMundo(Laberinto laberintoActual){
        Position posicionActual = this.jugador.getPosition();
//        Direction dir =this.jugador.getFacingDir(); // Este parametro dira exactamente con que se quiere interactuar 
        // Por el momento interactua con las 4 direcciones
        Position topPos = this.jugador.getPosition().copy().move(Direction.UP);
        Position rightPos = this.jugador.getPosition().copy().move(Direction.RIGHT);
        Position downPos = this.jugador.getPosition().copy().move(Direction.DOWN);
        Position leftPos = this.jugador.getPosition().copy().move(Direction.LEFT);
        this.interactuar(laberintoActual, topPos);
        this.interactuar(laberintoActual, rightPos);
        this.interactuar(laberintoActual, downPos);
        this.interactuar(laberintoActual, leftPos);
    }
    
    private void playerFaceDirection(String mov)
    {
        Direction dir = Direction.valueOf(mov);
        this.jugador.setFacingDir(dir);
    }
    
    private void pauseScreen()
    {
        System.out.println("Presione Enter para continuar...");
        Scanner scan = new Scanner(System.in);
        scan.nextLine();
    }
    
    public enum Result
    {
        QUIT,
        LOSE,
        WIN,
        PLAYING
    }
}
