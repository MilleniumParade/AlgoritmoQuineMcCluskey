import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/** Algoritmo de QuineMcCluskey **/

public class Quine {
    private int numeroVariables;
    private int numeroMinterminos;
    private int minterminosDecimal[];
    private String minterminosBinario[];
    private boolean[] marcado; // Para marcar si un minterm ha sido combinado
    private List<List<String>> grupos;
    private List<String> primosImplicantes; // Lista para almacenar los primos implicantes finales
    private List<String> mintermsOriginales; // Nueva lista para guardar minterms originales
    Scanner sc = new Scanner(System.in);

    public Quine(int numeroVariables, int minterminosDecimal[]) {
        this.numeroVariables = numeroVariables;
        this.minterminosDecimal = minterminosDecimal;
        this.numeroMinterminos = minterminosDecimal.length;
        this.minterminosBinario = new String[numeroMinterminos];
        this.marcado = new boolean[numeroMinterminos]; // Inicializamos el arreglo de marcación
        this.grupos = new ArrayList<>();
        this.primosImplicantes = new ArrayList<>(); // Inicializamos la lista de primos implicantes
        this.mintermsOriginales = new ArrayList<>(); // Inicializamos la lista de minterms originales
    }

    /* Método para pasar un minterm de decimal a binario */
    public void decimalABinario() {
        for (int i = 0; i < minterminosDecimal.length; i++) {
            minterminosBinario[i] = String
                    .format("%" + numeroVariables + "s", Integer.toBinaryString(minterminosDecimal[i]))
                    .replace(' ', '0');
            mintermsOriginales.add(minterminosBinario[i]); // Añadimos el minterm binario original a la lista
        }
    }

    /* Cuenta los 1's de un mintermino */
    public int[] contarUnos(String[] minterminos) {
        int[] arr = new int[minterminos.length]; // Ajustado para usar el tamaño correcto de minterms
        int count = 0;
        for (int i = 0; i < minterminos.length; i++) {
            for (char c : String.valueOf(minterminos[i]).toCharArray()) {
                if (c == '1') {
                    count++;
                }
            }
            arr[i] = count;
            count = 0;
        }
        return arr;
    }

    /* Agrupa los minterms de acuerdo a los 1's */
    public void organizar(String[] minterminos) {
        grupos.clear(); // Limpiamos los grupos
        // Inicializar una lista para cada grupo (0 a numeroVariables de unos)
        for (int i = 0; i <= numeroVariables; i++) {
            grupos.add(new ArrayList<>());
        }
        // Obtener el conteo de 1's de cada mintermino
        int[] cuentaUnos = contarUnos(minterminos);

        // Agrupar los minterms según el número de unos en su representación binaria
        for (int i = 0; i < minterminos.length; i++) { // Ajustado para usar el tamaño correcto
            if (i < cuentaUnos.length) { // Verificamos que el índice no exceda el tamaño
                int numUnos = cuentaUnos[i]; // Número de 1's en el mintermino actual
                grupos.get(numUnos).add(minterminos[i]); // Agregar el mintermino binario al grupo correcto
            }
        }

        // Mostrar los grupos
        for (int i = 0; i <= numeroVariables; i++) {
            if (!grupos.get(i).isEmpty()) {
                System.out.println("Grupo con " + i + " unos: " + grupos.get(i));
            }
        }
    }

    /** Combinar minterms que difieren en un solo bit **/
    public List<String> combinarMinterms(List<String> minterms) {
        System.out.println("\nCombinando minterms que difieren en un solo bit:");
        List<String> combinaciones = new ArrayList<>();
        boolean combinado = false; // Indica si se hizo alguna combinación en esta pasada

        for (int i = 0; i < grupos.size() - 1; i++) {
            List<String> grupoActual = grupos.get(i);
            List<String> grupoSiguiente = grupos.get(i + 1);

            // Comparar cada elemento del grupo actual con cada elemento del grupo siguiente
            for (String minterm1 : grupoActual) {
                for (String minterm2 : grupoSiguiente) {
                    String combinacion = combinarSiDifiereEnUnBit(minterm1, minterm2);
                    if (combinacion != null) {
                        combinado = true; // Se hizo una combinación
                        combinaciones.add(combinacion);
                        marcarCombinado(minterm1);
                        marcarCombinado(minterm2);
                        System.out.println(minterm1 + " combinado con " + minterm2 + " = " + combinacion);
                    }
                }
            }
        }

        // Si no se hicieron combinaciones, los minterms restantes son primos implicantes
        if (!combinado) {
            for (String minterm : minterms) {
                if (!primosImplicantes.contains(minterm)) {
                    primosImplicantes.add(minterm);
                }
            }
        }

        return combinaciones; // Devuelve las combinaciones generadas
    }

    /* Método para marcar los minterms que fueron combinados */
    public void marcarCombinado(String minterm) {
        // Comprobar si el minterm está en los originales
        if (mintermsOriginales.contains(minterm)) {
            int index = mintermsOriginales.indexOf(minterm); // Obtener el índice del minterm original
            if (index < marcado.length) {
                marcado[index] = true; // Marcarlo si el índice es válido
            }
        }
    }

    /* Método para combinar dos minterms si difieren en exactamente un bit */
    public String combinarSiDifiereEnUnBit(String minterm1, String minterm2) {
        int diferencia = 0;
        StringBuilder combinacion = new StringBuilder();

        for (int i = 0; i < minterm1.length(); i++) {
            if (minterm1.charAt(i) != minterm2.charAt(i)) {
                diferencia++;
                combinacion.append('-'); // Si difieren, se pone un guion
            } else {
                combinacion.append(minterm1.charAt(i)); // Si son iguales, se mantiene el bit
            }
            // Si la diferencia es mayor que 1, no se pueden combinar
            if (diferencia > 1) {
                return null;
            }
        }
        // Si solo difieren en un bit, devolvemos la combinación
        return (diferencia == 1) ? combinacion.toString() : null;
    }

    public void hacerTodo() {
        decimalABinario();
        boolean seHizoCombinacion;
        List<String> mintermsCombinados = new ArrayList<>();

        do {
            // Convertimos la lista de minterms combinados a un arreglo de Strings
            String[] mintermsParaOrganizar = mintermsCombinados.isEmpty() ? minterminosBinario : mintermsCombinados.toArray(new String[0]);
            organizar(mintermsParaOrganizar);
            mintermsCombinados = combinarMinterms(mintermsCombinados.isEmpty() ? List.of(minterminosBinario) : mintermsCombinados);
            if (!mintermsCombinados.isEmpty()) {
                // Actualizar los minterms binarios con las nuevas combinaciones para la siguiente ronda
                minterminosBinario = mintermsCombinados.toArray(new String[0]);
                numeroMinterminos = mintermsCombinados.size(); // Actualizamos el número de minterms
            }
            seHizoCombinacion = !mintermsCombinados.isEmpty();
        } while (seHizoCombinacion); // Continuar hasta que no se hagan más combinaciones

        // Mostrar los primos implicantes
        System.out.println("\nPrimos implicantes:");
        for (String primo : primosImplicantes) {
            System.out.println(primo);
        }
    }

    public static void main(String[] args) {
        int[] ints = { 1, 3, 4, 5, 9, 11, 12, 13, 14, 15 };
        Quine quine = new Quine(4, ints);
        quine.hacerTodo();
    }
}