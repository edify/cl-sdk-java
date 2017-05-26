package org.commonlibrary.clsdk.lo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.json.JsonSlurper
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.commonlibrary.clauth.SAuthc1Signer
import org.commonlibrary.clauth.model.ApiKeyCredentials

import java.util.concurrent.TimeUnit

/**
 * Created by diugalde on 20/05/17.
 */
class LearningObject {

    private OkHttpClient client
    private def apiUrl
    private def baseUrl
    private ApiKeyCredentials apiKeyCredentials
    private SAuthc1Signer sAuthc1Signer
    private static final def NONCE_LENGTH = 15
    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8")
    private ObjectMapper mapper
    private def jsonSlurper

    LearningObject(ApiKeyCredentials apiKeyCredentials, baseUrl, apiUrl) {
        client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        this.baseUrl = baseUrl
        this.apiUrl = apiUrl
        this.apiKeyCredentials = apiKeyCredentials
        sAuthc1Signer = new SAuthc1Signer()
        jsonSlurper = new JsonSlurper()
        mapper = new ObjectMapper()
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
    }

    def saveLearningObjective(learningObjective) {
        def path = "/learningObjectives"
        Response response = post(path, learningObjective)
        return buildMapResponse(response)
    }

    def save(learningObject) {
        def path = "/learningObjects"
        Response response = post(path, learningObject)
        return buildMapResponse(response)
    }

    def findById(id) {
        def path = """/learningObjects/${id}"""
        Response response = get(path)
        return buildMapResponse(response)
    }

    def storeContents(id, contents) {
        def path = """/learningObjects/${id}/file"""
        Response response = post(path, contents)
        return buildMapResponse(response)
    }

    InputStream getFileInputStream(learningObject) {
        def urlString = "${baseUrl}${apiUrl}${learningObject.contents.url}".toString()
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        def headersMap = [:]
        def nonce = Utils.generateRandomString(NONCE_LENGTH)
        sAuthc1Signer.sign(headersMap, "get", urlString, "", new Date(), apiKeyCredentials, nonce)
        headersMap.each() { name, value -> conn.setRequestProperty(name, value) }

        conn.getInputStream()
    }

    private def get(path, Map<String,String> params = null, apiUrl = this.apiUrl) {
        def url = """${baseUrl}${apiUrl}${path}"""
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()

        if (params) {
            params.each() { name, value -> urlBuilder.addQueryParameter(name, value) }
        }

        HttpUrl httpUrl = urlBuilder.build()
        def headers = generateAuthHeaders(httpUrl.toString(), "get", "")

        Request request = new Request.Builder().headers(headers).url(httpUrl).build()
        Response response = client.newCall(request).execute()

        return response
    }

    private def put(path, bodyObject, apiUrl = this.apiUrl) {
        def url = """${baseUrl}${apiUrl}${path}"""
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()

        def jsonString = "{}"
        if (bodyObject) {
            jsonString = mapper.writeValueAsString(bodyObject)
        }

        HttpUrl httpUrl = urlBuilder.build()
        def headers = generateAuthHeaders(httpUrl.toString(), "put", jsonString)
        RequestBody body = RequestBody.create(JSON_MEDIA, jsonString)

        Request request = new Request.Builder().headers(headers).url(httpUrl).put(body).build()
        Response response = client.newCall(request).execute();

        return response
    }

    private def post(path, bodyObject, apiUrl = this.apiUrl) {
        def url = """${baseUrl}${apiUrl}${path}"""
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()

        def jsonString = "{}"
        if (bodyObject) {
            jsonString = mapper.writeValueAsString(bodyObject)
        }

        HttpUrl httpUrl = urlBuilder.build()
        def headers = generateAuthHeaders(httpUrl.toString(), "post", jsonString)
        RequestBody body = RequestBody.create(JSON_MEDIA, jsonString)

        Request request = new Request.Builder().headers(headers).url(httpUrl).post(body).build()
        Response response = client.newCall(request).execute();

        return response
    }

    private def delete(path, apiUrl = this.apiUrl) {
        def url = """${baseUrl}${apiUrl}${path}"""
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()

        HttpUrl httpUrl = urlBuilder.build()
        def headers = generateAuthHeaders(httpUrl.toString(), "delete", "")

        Request request = new Request.Builder().headers(headers).url(httpUrl).delete().build()
        Response response = client.newCall(request).execute()

        return response
    }

    private Headers generateAuthHeaders(url, method, body) {
        body = body.replaceAll("[ \n]", "")
        def headersMap = [:]
        def date = new Date()
        def nonce = Utils.generateRandomString(NONCE_LENGTH)

        sAuthc1Signer.sign(headersMap, method, url, body, date, apiKeyCredentials, nonce)

        Headers.Builder headersBuilder = new Headers.Builder()
        headersMap.each() { name, value -> headersBuilder.add(name, value.toString()) }

        return headersBuilder.build()
    }

    private def buildMapResponse(Response response, expectedStatusCode = 200) {
        def closedBody = false
        try {
            if (response.code() != expectedStatusCode) {
                def errorMsg = "Request to cl-lo returned ${response.code()} status."
                throw new Exception(errorMsg.toString())
            }

            def bodyString = response.body().string()
            closedBody = true
            return jsonSlurper.parseText(bodyString)
        } finally {
            if (response && !closedBody) {
                response.body().close()
            }
        }
    }
}
