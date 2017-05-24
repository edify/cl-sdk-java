# cl-sdk-java-curricula

CL Curricula SDK

---

This guide gives the user working code examples about how to interact with the CL - Curricula micro service.


## General usage

1.  Import in gradle

    ```bash
    compile "org.commonlibrary:cl-sdk-java-curricula:0.0.1"
    ```


2.  First, you need to import the sdk client and create a new instance. In order to obtain a new Curricula object, you need to provide valid credentials.

    ```groovy
    import org.commonlibrary.clsdk.curricula.Curricula
    
    public static void main(String [] args) {
        String apiKeyId = "98afcdf62b5ccb73c35b8b2abc061af4"
        String apiSecretKey = "148760d7fd8e9a205e21eefedda352773dd3c925"
        ApiKeyCredentials apiKeyCredentials = new ApiKeyCredentials(apiKeyId, apiSecretKey)
        String baseUrl = "http://localhost:8081"
        String apiUrl = "/api/v1"
        Curricula c = new Curricula(apiKeyCredentials, baseUrl, apiUrl)
    }
    ```

3.  Supported methods:

### Save curriculum

#### Code

```groovy
def curriculumObject = [
    name: "CurriculumName",
    title: "title",
    discipline: "discipline",
    description: "description",
    enabled: true,
    metadata: [
        keywords: "anyKeyWordUpdated",
        coverage: "anyCoverage",
        context: "ANY",
        difficulty: "ANY",
        endUser: "ANY",
        interactivityDegree: "ANY",
        language: "ENGLISH",
        status: "ANY",
        author: "anyAuthor",
        topic: "anyTopic",
        isbn: "anyISBN",
        price: 2000,
        extraMetadata: ["extraMetadata1", "extraMetadata2"]
    ]
]
println c.save(curriculumObject)
```

#### Response

```groovy
[description:description, discipline:discipline, enabled:true, id:611b3e59-e390-42c9-a6e9-3cc36407afc4, metadata:[author:anyAuthor, context:ANY, coverage:anyCoverage, difficulty:ANY, endUser:ANY, extraMetadata:[extraMetadata1, extraMetadata2], interactivityDegree:ANY, isbn:anyISBN, keywords:anyKeyWordUpdated, language:ENGLISH, price:2000, status:ANY, topic:anyTopic], name:CurriculumName, title:title]
```



### Find curriculum by id

#### Code

```groovy
def curriculumId = "611b3e59-e390-42c9-a6e9-3cc36407afc4"
println c.findById(curriculumId)
```

#### Response

```groovy
[description:description, discipline:discipline, enabled:true, id:611b3e59-e390-42c9-a6e9-3cc36407afc4, metadata:[author:anyAuthor, context:ANY, coverage:anyCoverage, difficulty:ANY, endUser:ANY, extraMetadata:[extraMetadata1, extraMetadata2], interactivityDegree:ANY, isbn:anyISBN, keywords:anyKeyWordUpdated, language:ENGLISH, price:2000, status:ANY, topic:anyTopic], name:CurriculumName, title:title]
```


### Find multiple curricula

#### Code

```groovy
def from = 0
def size = 3
def all = false
println c.find(from, size, all)
```

#### Response

```groovy
[content:[[description:description, discipline:discipline, enabled:true, id:268f72e8-af51-4d63-91b2-48b99b82362f, metadata:[author:anyAuthor, context:ANY, coverage:anyCoverage, difficulty:ANY, endUser:ANY, extraMetadata:[extraMetadata1, extraMetadata2], interactivityDegree:ANY, isbn:anyISBN, keywords:anyKeyWordUpdated, language:ENGLISH, price:2000, status:ANY, topic:anyTopic], name:c1, title:title], [description:description, discipline:discipline, enabled:true, id:b142d06b-9f9e-4ab0-b552-b64c09b646f5, metadata:[author:anyAuthor, context:ANY, coverage:anyCoverage, difficulty:ANY, endUser:ANY, extraMetadata:[extraMetadata1, extraMetadata2], interactivityDegree:ANY, isbn:anyISBN, keywords:anyKeyWordUpdated, language:ENGLISH, price:2000, status:ANY, topic:anyTopic], name:groovyCurriculum, title:title], [description:description, discipline:discipline, enabled:true, id:8e33b137-4db2-4d47-b3a5-da674fe11072, metadata:[author:anyAuthor, context:ANY, coverage:anyCoverage, difficulty:ANY, endUser:ANY, extraMetadata:[extraMetadata1, extraMetadata2], interactivityDegree:ANY, isbn:anyISBN, keywords:anyKeyWordUpdated, language:ENGLISH, price:2000, status:ANY, topic:anyTopic], name:c2, title:title C2]], firstPage:true, lastPage:false, numberOfElements:3, totalElements:5, totalPages:2]
```


### Add folder

#### Code

```groovy
def curriculumId = "611b3e59-e390-42c9-a6e9-3cc36407afc4"
def folderPath = "/folder1"
def folderName = "newFolder"
println c.addFolder(curriculumId, folderPath, folderName)
```

#### Response

```groovy
[id:daa4759d-3567-4f3e-b0bd-a9d9d18c020c, name:newFolder]
```


### Add LearningObject

#### Code

```groovy
def curriculumId = "611b3e59-e390-42c9-a6e9-3cc36407afc4"
def folderPath = "/folder1/newFolder"
def learningObject = [
     name: "LO3",
     title: "WHAT?",
     url: "/learningObjects/591f31d172d4d3536e91e583",
     learningObjectId: "591f31d172d4d3536e91e583",
     contentUrl: "/learningObjects/591f31d172d4d3536e91e583/contents/591f349872d4d3536e91e584/file/filename.xml?refPath=591f31d172d4d3536e91e583/",
     learningObjectives: []
 ]
println c.addLearningObject(curriculumId, folderPath, learningObject)
```

#### Response

```groovy
[contentUrl:/learningObjects/591f31d172d4d3536e91e583/contents/591f349872d4d3536e91e584/file/filename.xml?refPath=591f31d172d4d3536e91e583/, deleted:false, id:affe75f2-4fef-470c-bfc3-551cf65105a5, learningObjectId:591f31d172d4d3536e91e583, learningObjectives:[], name:LO3, title:WHAT?, updated:false, url:/learningObjects/591f31d172d4d3536e91e583]
```


### Get Folder

#### Code

```groovy
def curriculumId = "611b3e59-e390-42c9-a6e9-3cc36407afc4"
def itemPath = "/folder1/newFolder"
println c.getFolder(curriculumId, itemPath, learningObjects = false, subFolders = false)
```

#### Response

```groovy
[id:daa4759d-3567-4f3e-b0bd-a9d9d18c020c, name:newFolder]
```


### Get LearningObject

#### Code

```groovy
def curriculumId = "611b3e59-e390-42c9-a6e9-3cc36407afc4"
def itemPath = "/folder1/newFolder/LO3"
println c.getLearningObject(curriculumId, itemPath, false, false)
```

#### Response

```groovy
[contentUrl:/learningObjects/591f31d172d4d3536e91e583/contents/591f349872d4d3536e91e584/file/filename.xml?refPath=591f31d172d4d3536e91e583/, deleted:false, id:affe75f2-4fef-470c-bfc3-551cf65105a5, learningObjectId:591f31d172d4d3536e91e583, learningObjectives:[], name:LO3, title:WHAT?, updated:false, url:/learningObjects/591f31d172d4d3536e91e583]
```


### Synchronize Updated LearningObjects

#### Code

```groovy
def learningObjectId = "591f31d172d4d3536e91e583"
println c.syncUpdatedLearningObjects(learningObjectId)
```

#### Response

```groovy
[updatedRecords:2]
```


### Synchronize Deleted LearningObjects

#### Code

```groovy
def learningObjectId = "591f31d172d4d3536e91e583"
println c.syncDeletedLearningObjects(learningObjectId)
```

#### Response

```groovy
[updatedRecords:2]
```
