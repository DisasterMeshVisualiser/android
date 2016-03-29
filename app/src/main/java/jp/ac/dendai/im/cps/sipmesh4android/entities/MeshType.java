package jp.ac.dendai.im.cps.sipmesh4android.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeshType {
    private static final String TAG = MeshType.class.getSimpleName();

    private ArrayList<MeshTypeData> data = new ArrayList<>();

    public ArrayList<MeshTypeData> getData() {
        return data;
    }

    public void setData(ArrayList<MeshTypeData> data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeshTypeData {

        private int id;
        private String name;
        private String label;
        private boolean isChecked;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setIsChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        @Override
        public String toString() {
            return "MeshTypeData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", label='" + label + '\'' +
                    ", isChecked=" + isChecked +
                    '}';
        }
    }
}
