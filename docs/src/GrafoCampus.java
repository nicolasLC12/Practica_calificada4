import java.util.*;

public class GrafoCampus {

    private Map<String, List<Conexion>> grafo = new LinkedHashMap<>();
    private Map<String, Integer> heuristicaLaboratorio = new HashMap<>();

    public GrafoCampus() {
        cargarNodos();
        cargarConexiones();
        cargarHeuristica();
    }

    private void cargarNodos() {
        agregarNodo("Entrada Principal");
        agregarNodo("Biblioteca");
        agregarNodo("Cafeteria");
        agregarNodo("Estacionamiento");
        agregarNodo("Laboratorio IA");
        agregarNodo("Aulas");
        agregarNodo("Secretaria");
        agregarNodo("Auditorio");
    }

    private void cargarConexiones() {
        // Grafo no dirigido y ponderado. El peso representa minutos aproximados.
        agregarConexion("Entrada Principal", "Biblioteca", 4);
        agregarConexion("Entrada Principal", "Cafeteria", 6);
        agregarConexion("Entrada Principal", "Estacionamiento", 3);

        agregarConexion("Biblioteca", "Laboratorio IA", 5);
        agregarConexion("Biblioteca", "Aulas", 4);

        agregarConexion("Cafeteria", "Aulas", 3);
        agregarConexion("Cafeteria", "Secretaria", 5);
        agregarConexion("Cafeteria", "Estacionamiento", 4);

        agregarConexion("Laboratorio IA", "Aulas", 2);
        agregarConexion("Laboratorio IA", "Auditorio", 6);

        agregarConexion("Aulas", "Secretaria", 4);
        agregarConexion("Secretaria", "Auditorio", 3);
        agregarConexion("Estacionamiento", "Auditorio", 9);
    }

    private void cargarHeuristica() {
        // Estimacion simple de cercania hacia el Laboratorio IA.
        heuristicaLaboratorio.put("Entrada Principal", 7);
        heuristicaLaboratorio.put("Biblioteca", 4);
        heuristicaLaboratorio.put("Cafeteria", 5);
        heuristicaLaboratorio.put("Estacionamiento", 8);
        heuristicaLaboratorio.put("Aulas", 2);
        heuristicaLaboratorio.put("Secretaria", 5);
        heuristicaLaboratorio.put("Auditorio", 6);
        heuristicaLaboratorio.put("Laboratorio IA", 0);
    }

    private void agregarNodo(String nodo) {
        grafo.putIfAbsent(nodo, new ArrayList<>());
    }

    private void agregarConexion(String origen, String destino, int minutos) {
        grafo.get(origen).add(new Conexion(destino, minutos));
        grafo.get(destino).add(new Conexion(origen, minutos));
    }

    public void mostrarGrafo() {
        System.out.println("GRAFO DEL CAMPUS (no dirigido y ponderado)");
        for (String nodo : grafo.keySet()) {
            System.out.print(nodo + " -> ");
            for (Conexion conexion : grafo.get(nodo)) {
                System.out.print(conexion.destino + "(" + conexion.minutos + " min)  ");
            }
            System.out.println();
        }
    }

    public List<String> recorridoDFS(String inicio) {
        List<String> recorrido = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        dfsRecursivo(inicio, visitados, recorrido);
        return recorrido;
    }

    private void dfsRecursivo(String actual, Set<String> visitados, List<String> recorrido) {
        visitados.add(actual);
        recorrido.add(actual);

        for (Conexion vecino : grafo.get(actual)) {
            if (!visitados.contains(vecino.destino)) {
                dfsRecursivo(vecino.destino, visitados, recorrido);
            }
        }
    }

    public List<String> recorridoBFS(String inicio) {
        List<String> recorrido = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();

        visitados.add(inicio);
        cola.add(inicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            recorrido.add(actual);

            for (Conexion vecino : grafo.get(actual)) {
                if (!visitados.contains(vecino.destino)) {
                    visitados.add(vecino.destino);
                    cola.add(vecino.destino);
                }
            }
        }
        return recorrido;
    }

    public ResultadoRuta buscarAStar(String inicio, String destino) {
        PriorityQueue<NodoAStar> abiertos = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<String, Integer> costoAcumulado = new HashMap<>();
        Map<String, String> anterior = new HashMap<>();
        Set<String> cerrados = new HashSet<>();

        costoAcumulado.put(inicio, 0);
        abiertos.add(new NodoAStar(inicio, 0, obtenerHeuristica(inicio)));

        while (!abiertos.isEmpty()) {
            NodoAStar actual = abiertos.poll();

            if (cerrados.contains(actual.nombre)) {
                continue;
            }

            if (actual.nombre.equals(destino)) {
                List<String> camino = reconstruirCamino(anterior, destino);
                return new ResultadoRuta(camino, costoAcumulado.get(destino));
            }

            cerrados.add(actual.nombre);

            for (Conexion vecino : grafo.get(actual.nombre)) {
                int nuevoCosto = costoAcumulado.get(actual.nombre) + vecino.minutos;

                if (!costoAcumulado.containsKey(vecino.destino)
                        || nuevoCosto < costoAcumulado.get(vecino.destino)) {

                    costoAcumulado.put(vecino.destino, nuevoCosto);
                    anterior.put(vecino.destino, actual.nombre);

                    int h = obtenerHeuristica(vecino.destino);
                    abiertos.add(new NodoAStar(vecino.destino, nuevoCosto, h));
                }
            }
        }

        return new ResultadoRuta(new ArrayList<>(), -1);
    }

    private int obtenerHeuristica(String nodo) {
        return heuristicaLaboratorio.getOrDefault(nodo, 0);
    }

    private List<String> reconstruirCamino(Map<String, String> anterior, String destino) {
        LinkedList<String> camino = new LinkedList<>();
        String actual = destino;

        while (actual != null) {
            camino.addFirst(actual);
            actual = anterior.get(actual);
        }

        return camino;
    }

    private String unirRecorrido(List<String> recorrido) {
        return String.join(" -> ", recorrido);
    }

    public static void main(String[] args) {
        GrafoCampus campus = new GrafoCampus();

        campus.mostrarGrafo();

        System.out.println("\nRECORRIDO DFS desde Entrada Principal:");
        System.out.println(campus.unirRecorrido(campus.recorridoDFS("Entrada Principal")));

        System.out.println("\nRECORRIDO BFS desde Entrada Principal:");
        System.out.println(campus.unirRecorrido(campus.recorridoBFS("Entrada Principal")));

        System.out.println("\nBUSQUEDA A* desde Entrada Principal hasta Laboratorio IA:");
        ResultadoRuta ruta = campus.buscarAStar("Entrada Principal", "Laboratorio IA");
        System.out.println("Ruta recomendada: " + campus.unirRecorrido(ruta.camino));
        System.out.println("Tiempo total estimado: " + ruta.costoTotal + " minutos");
    }

    static class Conexion {
        String destino;
        int minutos;

        Conexion(String destino, int minutos) {
            this.destino = destino;
            this.minutos = minutos;
        }
    }

    static class NodoAStar {
        String nombre;
        int g; // costo real acumulado
        int h; // heuristica estimada
        int f; // g + h

        NodoAStar(String nombre, int g, int h) {
            this.nombre = nombre;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }

    static class ResultadoRuta {
        List<String> camino;
        int costoTotal;

        ResultadoRuta(List<String> camino, int costoTotal) {
            this.camino = camino;
            this.costoTotal = costoTotal;
        }
    }
}
