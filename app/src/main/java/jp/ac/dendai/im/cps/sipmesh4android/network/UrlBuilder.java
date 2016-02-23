package jp.ac.dendai.im.cps.sipmesh4android.network;

import okhttp3.HttpUrl;

/**
 * Created by hiro on 12/15/15.
 */
public class UrlBuilder {
    public static final String DON_SCHEME = "http";
    public static final String DON_HOST = "www.j-shis.bosai.go.jp";
    public static final String ROOT = "map";
    public static final String API = "api";
    public static final String MESH_SEARCH = "meshsearch";

    /**
     * /map/api/meshsearch/
     * @return
     */
    public static HttpUrl.Builder buildRootUrl() {
        return new HttpUrl.Builder()
                .scheme(DON_SCHEME)
                .host(DON_HOST)
                .addPathSegment(ROOT)
                .addPathSegment(API);
    }

    public static HttpUrl.Builder buildMeshSearchUrl() {
        return buildRootUrl().addPathSegment(MESH_SEARCH);
    }

}
