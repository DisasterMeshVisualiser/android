package jp.ac.dendai.im.cps.sipmesh4android.network;

import okhttp3.HttpUrl;

/**
 * Created by hiro on 12/15/15.
 */
public class UrlBuilder {
    public static final String DON_SCHEME = "http";
    public static final String DON_HOST = "www.j-shis.bosai.go.jp";
    public static final String DON_ROOT = "map";
    public static final String DON_API = "api";
    public static final String MESH_SEARCH = "meshsearch";

    public static final String SCHEME = "http";
    public static final String HOST = "mesh.cps.im.dendai.ac.jp";
    public static final String API = "api";
    public static final String ROOT = "v1";
    public static final String CPS_MESH = "mesh.json";
    public static final String CPS_MESH_TYPE = "mesh_type.json";

    /**
     * /map/api/
     * @return HttpUrl.Builder()
     */
    private static HttpUrl.Builder buildRootUrl() {
        return new HttpUrl.Builder()
                .scheme(DON_SCHEME)
                .host(DON_HOST)
                .addPathSegment(DON_ROOT)
                .addPathSegment(DON_API);
    }

    /**
     * /api/v1/
     * @return HttpUrl.Builder()
     */
    private static HttpUrl.Builder buildCpsRootUrl() {
        return new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .addPathSegment(API)
                .addPathSegment(ROOT);
    }

    /**
     * /map/api/meshsearch
     * @return HttpUrl.Builder()
     */
    public static HttpUrl.Builder buildMeshSearchUrl() {
        return buildRootUrl().addPathSegment(MESH_SEARCH);
    }

    /**
     * http://mesh.cps.im.dendai.ac.jp/api/v1/mesh.json
     * @return HttpUrl.Builder()
     */
    public static HttpUrl.Builder buildCpsMeshUrl() {
        return buildCpsRootUrl().addPathSegment(CPS_MESH);
    }

    /**
     * http://mesh.cps.im.dendai.ac.jp/api/v1/mesh_type.json
     * @return HttpUrl.Builder()
     */
    public static HttpUrl.Builder buildCpsMeshTypeUrl() {
        return buildCpsRootUrl().addPathSegment(CPS_MESH_TYPE);
    }

}
