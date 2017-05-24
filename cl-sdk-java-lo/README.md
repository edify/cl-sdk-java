# cl-sdk-java-lo

CL LearningObject SDK

---

This guide gives the user working code examples about how to interact with the CL - LearningObjects micro service.


## General usage

1.  Import in gradle

    ```bash
    compile "org.commonlibrary:cl-sdk-lo:0.0.1"
    ```


2.  First, you need to import the sdk client and create a new instance. In order to obtain a new LearningObject object, you need to provide valid credentials.

    ```groovy
    import org.commonlibrary.clsdk.lo.LearningObject
    
    public static void main(String [] args) {
        String apiKeyId = "98afcdf62b5ccb73c35b8b2abc061af4"
        String apiSecretKey = "148760d7fd8e9a205e21eefedda352773dd3c925"
        ApiKeyCredentials apiKeyCredentials = new ApiKeyCredentials(apiKeyId, apiSecretKey)
        String baseUrl = "http://localhost:808"
        String apiUrl = "/api/v1"
        LearningObject lo = new LearningObject(apiKeyCredentials, baseUrl, apiUrl)
    }
    ```

3.  Supported methods:

### Save learningObjective

#### Code

```groovy
def learningObjective = [
    name: "New learning objective name",
    description: "description"
]
println lo.saveLearningObjective(learningObjective)
```

#### Response

```groovy
[creationDate:1495619173918, description:description, id:5925566574d4d270c3f5243c, learningObjectiveList:[], modificationDate:1495619173918, name:New learning objective name, new:false]
```


### Save learningObject

#### Code

```groovy
def learningObject = [
    name: "LearningObject name",
    description: "LO description",
    type: "EXAM",
    title: "LO Title",
    enabled: true,
    metadata: [
        coverage: "Learning Object coverage",
        context: "PRIMARY_EDUCATION",
        difficulty: "VERY_LOW",
        endUser: "LEARNERS",
        language: "English",
        status: "FINAL",
        author: "Learning Object author",
        isbn: "Learning Object ISBN",
        price: "100",
        extraMetadata: ["Additional metadata key,Additional metadata value"]
    ]
]
println lo.save(learningObject)
```

#### Response

```groovy
[compoundContent:false, contents:null, creationDate:1495619479975, description:LO description, enabled:true, externalUrl:null, format:null, id:5925579874d4d270c3f5243d, isPublic:true, learningObjectiveList:[], metadata:[author:Learning Object author, context:PRIMARY_EDUCATION, coverage:Learning Object coverage, difficulty:VERY_LOW, endUser:LEARNERS, extraMetadata:[Additional metadata key,Additional metadata value], interactivityDegree:null, isbn:Learning Object ISBN, keywords:null, language:English, price:100.0, status:FINAL, topic:null], modificationDate:1495619479975, name:LearningObject name, new:false, subject:null, title:LO Title, type:EXAM]
```


### Find learningObject by id

#### Code

```groovy
def loId = "5925579874d4d270c3f5243d"
println lo.findById(loId)
```

#### Response

```groovy
[compoundContent:false, contents:[creationDate:1495611846927, id:592539c674d4a0d28c9a8bab, md5:null, mimeType:text/html, modificationDate:1495611847586, new:false, resourcesURL:[], url:/learningObjects/592539c674d4a0d28c9a8baa/contents/592539c674d4a0d28c9a8bab/file/Conclusion.html?refPath=592539c674d4a0d28c9a8baa/], creationDate:1495611846915, description:null, enabled:true, externalUrl:null, format:HTML, id:592539c674d4a0d28c9a8baa, isPublic:true, learningObjectiveList:[], metadata:[author:null, context:ANY, coverage:null, difficulty:ANY, endUser:ANY, extraMetadata:[priority,9, type,INFORMATION], interactivityDegree:ANY, isbn:null, keywords:null, language:English, price:0.0, status:ANY, topic:null], modificationDate:1495611847588, name:Conclusion, new:false, subject:null, title:Conclusion, type:null]
```


### Store learningObject's contents

#### Code

```groovy
def contents = [
    filename: "filename.xml",
    md5: "5d39af9a571b3166fe88aad88bd043bc",
    mimeType: "text/xml",
    base64Content: "PHF1ZXN0ZXN0aW50ZXJvcCB4bWxucz0na+"
]
println lo.storeContents("5925579874d4d270c3f5243d", contents)
```

#### Response

```groovy
[md5:null, mimeType:text/xml, url:/learningObjects/5925579874d4d270c3f5243d/contents/5925594474d4d270c3f5243e/file/filename.xml?refPath=5925579874d4d270c3f5243d/]
```


### Get file's inputStream

#### Code

```groovy
def learningObject = lo.findById("5925579874d4d270c3f5243d").body
println lo.getFileInputStream(learObj).text
```

#### Response

```
<questestinterop xmlns='k
```
