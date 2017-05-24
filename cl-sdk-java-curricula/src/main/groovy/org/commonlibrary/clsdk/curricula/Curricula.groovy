package org.commonlibrary.clsdk.curricula

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

/**
 * Created by diugalde on 20/05/17.
 */
class Curricula {

    private OkHttpClient client
    private def apiUrl
    private def baseUrl
    private ApiKeyCredentials apiKeyCredentials
    private SAuthc1Signer sAuthc1Signer
    private static final def NONCE_LENGTH = 15
    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8")
    private ObjectMapper mapper
    private def jsonSlurper

    Curricula(ApiKeyCredentials apiKeyCredentials, baseUrl, apiUrl) {
        client = new OkHttpClient();
        this.baseUrl = baseUrl
        this.apiUrl = apiUrl
        this.apiKeyCredentials = apiKeyCredentials
        sAuthc1Signer = new SAuthc1Signer()
        jsonSlurper = new JsonSlurper()
        mapper = new ObjectMapper()
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
    }

    def save(curriculum) {
        def path = """/curricula"""
        Response response = post(path, curriculum)
        return buildMapResponse(response)
    }

    def findById(curriculumId) {
        def path = """/curricula/${curriculumId}"""
        Response response = get(path)
        return buildMapResponse(response)
    }

    def find(from, size, all) {
        def path = """/curricula"""

        def queryParams = [
            from: from.toString(),
            size: size.toString(),
            all: all.toString()
        ]

        Response response = get(path, queryParams)
        return buildMapResponse(response)
    }

    def addLearningObject(curriculumId, folderPath, learningObject) {
        folderPath = folderPath == "/" ? "" : folderPath
        def path = """/curricula/${curriculumId}/folders/path${folderPath}"""
        Response response = post(path, learningObject)
        return buildMapResponse(response)
    }

    def addFolder(curriculumId, folderPath, folderName) {
        folderPath = folderPath == "/" ? "" : folderPath
        def path = """/curricula/${curriculumId}/folders/path${folderPath}"""
        def folder = [name: folderName]
        Response response = post(path, folder)
        return buildMapResponse(response)
    }

    def getLearningObject(curriculumId, itemPath, contents = false, metadata = false) {
        itemPath = itemPath == "/" ? "" : itemPath
        def path = """/curricula/${curriculumId}/folders/path${itemPath}"""

        def expandParams = []
        if (contents) {
            expandParams << "contents"
        }
        if (metadata) {
            expandParams << "metadata"
        }

        def queryParams = expandParams.empty ? null : [expand: expandParams.join(",")]

        Response response = get(path, queryParams)
        return buildMapResponse(response)
    }

    def getFolder(curriculumId, itemPath, learningObjects = false, subFolders = false) {
        itemPath = itemPath == "/" ? "" : itemPath
        def path = """/curricula/${curriculumId}/folders/path${itemPath}"""

        def expandParams = []
        if (learningObjects) {
            expandParams << "learningObjects"
        }
        if (subFolders) {
            expandParams << "folders"
        }

        def queryParams = expandParams.empty ? null : [expand: expandParams.join(",")]

        Response response = get(path, queryParams)
        return buildMapResponse(response)
    }

    def syncUpdatedLearningObjects(learningObjectId) {
        def path = """/learningObjects/${learningObjectId}"""
        Response response = put(path, null)
        return buildMapResponse(response)
    }

    def syncDeletedLearningObjects(learningObjectId) {
        def path = """/learningObjects/${learningObjectId}"""
        Response response = delete(path)
        return buildMapResponse(response)
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
        if (response.code() != expectedStatusCode) {
            def errorMsg = "Request to cl-curricula returned ${response.code()} status."
            throw new Exception(errorMsg.toString())
        }

        return jsonSlurper.parseText(response.body().string())
    }
}
