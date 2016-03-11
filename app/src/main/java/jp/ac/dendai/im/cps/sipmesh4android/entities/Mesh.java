package jp.ac.dendai.im.cps.sipmesh4android.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Mesh {
    private static final String TAG = Mesh.class.getSimpleName();

    private ArrayList<MeshData> data = new ArrayList<>();

    public ArrayList<MeshData> getData() {
        return data;
    }

    public void setData(ArrayList<MeshData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Mesh{" +
                "data=" + data +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeshData {
        private String meshcode;
        private double[][] coordinates;
        private double value;

        public String getMeshcode() {
            return meshcode;
        }

        public void setMeshcode(String meshcode) {
            this.meshcode = meshcode;
        }

        public double[][] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[][] coordinates) {
            this.coordinates = coordinates;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "MeshData{" +
                    "meshcode='" + meshcode + '\'' +
                    ", coordinates=" + Arrays.toString(coordinates) +
                    ", value=" + value +
                    '}';
        }
    }
}
