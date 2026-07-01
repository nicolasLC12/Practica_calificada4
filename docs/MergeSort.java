
import java.util.Arrays;
public class MergeSort {



    public void sort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            sort(arr, left, mid);      // Ordenar primera mitad
            sort(arr, mid + 1, right); // Ordenar segunda mitad

            merge(arr, left, mid, right); // Fusionar mitades
        }
}

    void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; ++i) L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) { arr[k] = L[i]; i++; k++; }
        while (j < n2) { arr[k] = R[j]; j++; k++; }
    }


    // --- Ejecución de la Actividad  ---
    public static void main(String[] args) {
        MergeSort ob = new MergeSort();


        int[] tiemposCampus = {12, 4, 8, 1, 15, 7, 3, 10};

        System.out.println("Vector original (desordenado): " + Arrays.toString(tiemposCampus));


        ob.sort(tiemposCampus, 0, tiemposCampus.length - 1);

        System.out.println("Tiempos ordenados de menor a mayor: " + Arrays.toString(tiemposCampus));
    }
}