# View Resolvers

### Design;

#### Simple Model
<!--
```puml
@startuml class

class Course {
  code :: String 
}

class Exam {
  course :: Course
  code :: String
}

Exam -> Course

@enduml
```
-->
![](docs/class.svg)


#### Simple Access
<!--
```puml
@startuml sequence

actor Client
participant Container
participant ContentNegotiatingViewResolver
participant MustacheViewResolver
participant MyPdfViewResolver
participant ExamsViewController
participant "[[https://github.com/johnlayton/nokescourt/blob/8b49c476af2b1d4f45fcc28622f1be795a52df45/src/main/java/org/nokescourt/BulkMvcApplication.java#L103 ExamService]]" as ExamService

Client -> Container : [get] /view/exam?format=html
activate Container
Container -> ExamsViewController : exams 
activate ExamsViewController
ExamsViewController -> ExamService : listExams 
activate ExamService
ExamsViewController <- ExamService 
deactivate ExamService
ExamsViewController -> Container : ModelAndView("exams")
deactivate ExamsViewController
Container -> Container : getViewResolver
Container -> ContentNegotiatingViewResolver : getView
ContentNegotiatingViewResolver -> MustacheViewResolver : getView
MustacheViewResolver -> ContentNegotiatingViewResolver
ContentNegotiatingViewResolver -> Container
Container -> Client 
deactivate Container

Client -> Container : [get] /view/exam?format=pdf
activate Container
Container -> ExamsViewController : exams 
activate ExamsViewController
ExamsViewController -> Container : ModelAndView("exams")
deactivate ExamsViewController
Container -> Container : getViewResolver
Container -> ContentNegotiatingViewResolver : getView
ContentNegotiatingViewResolver -> MyPdfViewResolver : getView
MyPdfViewResolver -> ContentNegotiatingViewResolver
ContentNegotiatingViewResolver -> Container
Container -> Client 
deactivate Container

@enduml
```
-->
![](docs/sequence.svg)

### Setup;

```shell
devbox install
```

### Generate Documentation;

```shell
devbox run plantuml -tsvg -o docs README.md
```

### Run;

```shell
devbox run gradle bootRun
```

### Test Rest Controller;

```shell
curl --header "Accept: application/json" "http://localhost:8080/rest/exams"
```

```shell
curl --header "Accept: application/xml" "http://localhost:8080/rest/exams"
```

### Test View Controller;

```shell
curl --header "Accept: application/json" "http://localhost:8080/view/exams"
```

```shell
curl --header "Accept: text/plain" "http://localhost:8080/view/exams"
```

```shell
curl --header "Accept: text/html" "http://localhost:8080/view/exams"
```

```shell
open http://localhost:8080/view/exams?format=html
```

```shell
open http://localhost:8080/view/exams?format=txt
```

```shell
open http://localhost:8080/view/exams?format=pdf
```
